package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import greenberg.moviedbshell.R
import greenberg.moviedbshell.base.BaseMovieListFragment
import greenberg.moviedbshell.extensions.extractArguments
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.MovieListState
import greenberg.moviedbshell.state.base.BaseMovieListState
import greenberg.moviedbshell.viewmodel.PopularMoviesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PopularMoviesFragment : BaseMovieListFragment<PopularMoviesViewModel, MovieListState>() {

    @Inject
    lateinit var popularMoviesViewModelFactory: PopularMoviesViewModel.Factory

    override val viewModel: PopularMoviesViewModel by viewModels {
        PopularMoviesViewModel.provideFactory(
            popularMoviesViewModelFactory,
            arguments?.extractArguments<MovieDetailArgs>(PAGE_ARGS)?.movieId ?: -1,
            Dispatchers.IO,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.text = getString(R.string.popular_row)
    }

    override fun registerObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.popularMovieState.collect {
                        updateMovieList(it)
                    }
                }
            }
        }
    }

    override fun updateMovieList(state: BaseMovieListState) {
        log("updateMovieList state is $state")
        when {
            state.isLoading -> {
                log("loading")
                hideError()
                hideMaxPages()
                if (state.pageNumber <= 1) {
                    hideMovies()
                    showLoading()
                } else {
                    showPageLoad()
                }
            }
            state.error != null -> {
                log("error: ${state.error}")
                // TODO: If there are already movies, don't hide the whole page? right?
//                hideMovies()
                if (state.shouldShowMaxPages) {
                    showMaxPages()
                } else {
                    showError(state.error!!)
                }
            }
            state.movieList.isNotEmpty() -> {
                log("success")
                hidePageLoad()
                if (state.shouldShowMaxPages) {
                    showMaxPages()
                }
                hideLoading()
                showMovies(state)
            }
        }
    }

    override fun onClickListener(movieId: Int) {
        navigate(
            R.id.action_popularMoviesFragment_to_movieDetailFragment,
            MovieDetailArgs(movieId),
        )
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }
}
