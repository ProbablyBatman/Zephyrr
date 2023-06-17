package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.adapters.CombinedItemsListAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.extensions.extractArguments
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.state.CombinedItemsArgs
import greenberg.moviedbshell.state.CombinedItemsState
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.TvDetailArgs
import greenberg.moviedbshell.viewmodel.CombinedItemsListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * TODO: repurpose this to be used for:
 * 1. Generic movie list fragment
 * 2. Generic tv list fragment
 * 3. combined movie and tv list fragment
 *
 * Until that's fixed, this copies a lot of code from [greenberg.moviedbshell.base.BaseMovieListFragment]
 */
@AndroidEntryPoint
class CombinedItemsListFragment : BaseFragment() {

    @Inject
    lateinit var gridListToggleState: () -> String

    @Inject
    lateinit var combinedItemsViewModelFactory: CombinedItemsListViewModel.Factory

    private val viewModel: CombinedItemsListViewModel by viewModels {
        CombinedItemsListViewModel.provideFactory(
            combinedItemsViewModelFactory,
            arguments?.extractArguments<CombinedItemsArgs>(PAGE_ARGS)?.ids ?: emptyList(),
            Dispatchers.IO,
        )
    }

    private lateinit var itemRecycler: RecyclerView
    private lateinit var combinedItemsListAdapter: CombinedItemsListAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var gridListHolder: ViewSwitcher
    private lateinit var gridToggle: ImageView
    private lateinit var listToggle: ImageView
    private lateinit var title: TextView
    private var loadingSnackbar: Snackbar? = null
    private var maxPagesSnackbar: Snackbar? = null
    private var errorSnackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.combined_list_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemRecycler = view.findViewById(R.id.combined_list_paged_recycler)
        combinedItemsListAdapter = CombinedItemsListAdapter(onClickListener = this::onClickListener)
        log("toggle is $gridListToggleState")
        gridLayoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        gridListHolder = view.findViewById(R.id.combined_grid_list_holder)
        gridToggle = view.findViewById(R.id.combined_grid_toggle)
        listToggle = view.findViewById(R.id.combined_list_toggle)
        if (gridListToggleState.invoke() == ZephyrrApplication.GRID_LIST_GRID_VALUE) {
            combinedItemsListAdapter.updateViewType(CombinedItemsListAdapter.ViewType.VIEW_TYPE_GRID)
            itemRecycler.layoutManager = gridLayoutManager
            // By default, grid shown first, just cycle to list if we're on grid already
            gridListHolder.showNext()
//            movieRecycler.layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.recycler_grid_anim_from_bottom)
            itemRecycler.layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.recycler_list_fall_down_anim)
        } else {
            combinedItemsListAdapter.updateViewType(CombinedItemsListAdapter.ViewType.VIEW_TYPE_LIST)
            itemRecycler.layoutManager = linearLayoutManager
            itemRecycler.layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.recycler_list_fall_down_anim)
        }
        itemRecycler.adapter = combinedItemsListAdapter
        // TODO: revisit this because it still calls multiple pages while scrolling
        itemRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                when (combinedItemsListAdapter.currentViewType) {
                    CombinedItemsListAdapter.ViewType.VIEW_TYPE_GRID -> {
                        if (gridLayoutManager.findLastVisibleItemPosition() == gridLayoutManager.itemCount - 1) {
                            viewModel.fetchItems()
                        }
                    }
                    CombinedItemsListAdapter.ViewType.VIEW_TYPE_LIST -> {
                        if (linearLayoutManager.findLastVisibleItemPosition() == linearLayoutManager.itemCount - 1) {
                            viewModel.fetchItems()
                        }
                    }
                }
            }
        })

        progressBar = view.findViewById(R.id.combined_list_progress_bar)
        title = view.findViewById(R.id.combined_list_page_title)
        gridListHolder.setOnClickListener {
            (activity?.application as ZephyrrApplication).toggleGridListView()
            gridListHolder.showNext()
            when (combinedItemsListAdapter.switchViewType()) {
                CombinedItemsListAdapter.ViewType.VIEW_TYPE_GRID -> {
                    itemRecycler.layoutManager = gridLayoutManager
                    rerunAnim(true)
                }
                CombinedItemsListAdapter.ViewType.VIEW_TYPE_LIST -> {
                    itemRecycler.layoutManager = linearLayoutManager
                    rerunAnim(false)
                }
            }
        }

        registerObservers()
    }

    private fun registerObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.combinedItemsState.collect {
                        updateCombinedList(it)
                    }
                }
            }
        }
    }

    private fun updateCombinedList(state: CombinedItemsState) {
        when {
            state.isLoading -> {
                log("loading")
                hideError()
                hideMaxPages()
                if (state.moviePageNumber <= 1 && state.tvPageNumber <= 1) {
                    hideItems()
                    showLoading()
                } else {
                    showPageLoad()
                }
            }
            state.error != null && state.combinedItemList.isEmpty() -> {
                log("error: ${state.error}")
                if (state.isMovieMaxPages && state.isTvMaxPages) {
                    showMaxPages()
                } else {
                    // TODO: silently swallowing some errors for now if it's not a blocking error
                    // no-op
                    showError(state.error)
                }
            }
            state.combinedItemList.isNotEmpty() -> {
                log("success")
                hidePageLoad()
                title.text = arguments?.extractArguments<CombinedItemsArgs>(PAGE_ARGS)?.pageTitle.orEmpty()
                if (state.isMovieMaxPages && state.isTvMaxPages) {
                    showMaxPages()
                }
                hideLoading()
                showItems(state)
            }
        }
    }

    private fun onClickListener(itemId: Int, mediaType: MediaType) {
        when (mediaType) {
            MediaType.MOVIE -> {
                navigate(
                    R.id.action_combinedItemsListFragment_to_movieDetailFragment,
                    MovieDetailArgs(itemId),
                )
            }
            MediaType.TV -> {
                navigate(
                    R.id.action_combinedItemsListFragment_to_tvDetailFragment,
                    TvDetailArgs(itemId),
                )
            }
            MediaType.PERSON -> {
                // TODO: figure this one out
                // no-op?
            }
            MediaType.UNKNOWN -> {
                // TODO: figure this one out
                // no-op?
            }
        }
    }

    private fun hideMaxPages() {
        log("Hide max pages")
        maxPagesSnackbar?.dismiss()
    }

    private fun showMaxPages() {
        log("Show max pages")
        maxPagesSnackbar = Snackbar.make(itemRecycler, getString(R.string.generic_max_pages_text), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.dismiss)) { maxPagesSnackbar?.dismiss() }
        if (maxPagesSnackbar?.isShown == false) {
            maxPagesSnackbar?.show()
        }
    }

    private fun hideError() {
        log("Hide error")
        errorSnackbar?.dismiss()
    }

    private fun showError(throwable: Throwable) {
        log("Showing Error")
        log(throwable)
        errorSnackbar = Snackbar.make(itemRecycler, getString(R.string.generic_error_text), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.retry)) {
                errorSnackbar?.dismiss()
                viewModel.fetchItems()
            }
        errorSnackbar?.show()
    }

    private fun hideLoading() {
        log("Hide loading")
        progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        log("Show loading")
        progressBar.visibility = View.VISIBLE
    }

    private fun hidePageLoad() {
        log("Hide page load")
        loadingSnackbar?.dismiss()
    }

    private fun showPageLoad() {
        log("Show page load")
        loadingSnackbar = Snackbar.make(itemRecycler, getString(R.string.generic_loading_text), Snackbar.LENGTH_INDEFINITE)
        loadingSnackbar?.show()
    }

    private fun showItems(state: CombinedItemsState) {
        log("Showing items with state: $state")
        title.visibility = View.VISIBLE
        itemRecycler.visibility = View.VISIBLE
        combinedItemsListAdapter.items = state.combinedItemList
        combinedItemsListAdapter.notifyItemRangeChanged(0, combinedItemsListAdapter.itemCount)
    }

    private fun hideItems() {
        log("Hide movies")
        itemRecycler.visibility = View.GONE
        title.visibility = View.GONE
    }

    private fun rerunAnim(isGrid: Boolean) {
        // TODO: why are these the same
        // Also why does the animation run like this
        itemRecycler.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            requireContext(),
            if (isGrid) {
                R.anim.recycler_list_fall_down_anim
            } else {
                R.anim.recycler_list_fall_down_anim
            },
        )
        combinedItemsListAdapter.notifyItemRangeChanged(0, combinedItemsListAdapter.itemCount)
        itemRecycler.scheduleLayoutAnimation()
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }
}
