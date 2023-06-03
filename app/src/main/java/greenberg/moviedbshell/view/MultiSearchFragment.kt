package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import greenberg.moviedbshell.R
import greenberg.moviedbshell.adapters.MultiSearchQueryItemsAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.state.CombinedItemsArgs
import greenberg.moviedbshell.state.MultiSearchState
import greenberg.moviedbshell.state.SearchResultsArgs
import greenberg.moviedbshell.viewmodel.MultiSearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MultiSearchFragment : BaseFragment() {

    @Inject
    lateinit var multiSearchViewModelFactory: MultiSearchViewModel.Factory

    private val viewModel: MultiSearchViewModel by navGraphViewModels(R.id.nav_graph) {
        MultiSearchViewModel.provideFactory(
            multiSearchViewModelFactory,
            Dispatchers.IO
        )
    }

    private lateinit var multiSearchInput: EditText
    private lateinit var multiSearchAddButton: Button
    private lateinit var multiSearchQueryItemsRecycler: RecyclerView
    private lateinit var multiSearchQueryItemsAdapter: MultiSearchQueryItemsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    // TODO: this name sucks so much lmao
    private lateinit var multiSearchSearchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zephyrr_multisearch_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        multiSearchInput = view.findViewById(R.id.multi_search_input)
        multiSearchAddButton = view.findViewById(R.id.multi_search_add_button)
        multiSearchSearchButton = view.findViewById(R.id.multi_search_button)

        linearLayoutManager = LinearLayoutManager(activity)
        multiSearchQueryItemsRecycler = view.findViewById(R.id.multi_search_query_items)
        multiSearchQueryItemsRecycler.layoutManager = linearLayoutManager
        multiSearchQueryItemsAdapter = MultiSearchQueryItemsAdapter(removeOnClickListener = this::removeOnClickListener)
        multiSearchQueryItemsRecycler.adapter = multiSearchQueryItemsAdapter

        // TODO: maybe figure out a way to use the viewmodel for this
        multiSearchAddButton.setOnClickListener {
            // Clear query after hitting add.
            viewModel.updateCurrentQuery("")
            navigate(
                R.id.action_multiSearchFragment_to_searchResultsFragment,
                SearchResultsArgs(
                    query = multiSearchInput.text.toString(),
                    usingMultiSearch = true
                )
            )
        }

        // TODO: is this right? should there be another way to get the latest set of queries?
        multiSearchSearchButton.setOnClickListener {
            navigate(
                R.id.action_multiSearchFragment_to_combinedItemsListFragment,
                CombinedItemsArgs(
                    viewModel.multiSearchState.value.queries.mapNotNull { it.id }
                )
            )
        }

        multiSearchInput.doOnTextChanged { text, _, _, _ ->
            viewModel.updateCurrentQuery(text.toString())
        }

        registerObservers()
    }

    override fun onStop() {
        super.onStop()
        // clear viewmodel here?
    }

    private fun registerObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.multiSearchState.collect {
                        updateMultiSearch(it)
                    }
                }
            }
        }
    }

    private fun updateMultiSearch(state: MultiSearchState) {
        log("state is $state")
        multiSearchInput.setText(state.currentQuery)
        multiSearchInput.setSelection(state.currentQuery.length)
        multiSearchAddButton.visibility = if (state.currentQuery.isNotBlank()) {
            View.VISIBLE
        } else {
            View.GONE
        }
        multiSearchQueryItemsAdapter.currentQueries = state.queries
        multiSearchQueryItemsAdapter.notifyItemRangeChanged(0, multiSearchQueryItemsAdapter.itemCount)
    }

    private fun removeOnClickListener(position: Int) {
        viewModel.removeQuery(position)
        multiSearchQueryItemsAdapter.notifyItemRemoved(position)
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }
}