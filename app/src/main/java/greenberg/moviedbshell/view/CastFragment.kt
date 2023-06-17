package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import greenberg.moviedbshell.R
import greenberg.moviedbshell.adapters.CastListAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.extensions.extractArguments
import greenberg.moviedbshell.state.CastState
import greenberg.moviedbshell.state.CastStateArgs
import greenberg.moviedbshell.state.PersonDetailArgs
import greenberg.moviedbshell.viewmodel.CastViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class CastFragment : BaseFragment() {

    // TODO: error handling for empty list?
    private val viewModel: CastViewModel by viewModels {
        CastViewModel.provideFactory(arguments?.extractArguments<CastStateArgs>(PAGE_ARGS)?.castMembers ?: emptyList())
    }

    private lateinit var castRecycler: RecyclerView
    private lateinit var castAdapter: CastListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cast_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        castRecycler = view.findViewById(R.id.cast_members_recycler)
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        castRecycler.layoutManager = layoutManager
        castAdapter = CastListAdapter(onClickListener = this::onClickListener)
        castRecycler.adapter = castAdapter

        registerObservers()
    }

    private fun registerObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.castState.collect {
                        showDetails(it)
                    }
                }
            }
        }
    }

    private fun showDetails(state: CastState) {
        if (state.castMembers.isNotEmpty()) {
            castAdapter.setCastMembers(state.castMembers)
        }
    }

    private fun onClickListener(personId: Int) {
        navigate(
            R.id.action_castFragment_to_personDetailFragment,
            PersonDetailArgs(personId),
        )
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }
}
