package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.View
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.base.BaseMovieListFragment
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.MovieListState
import greenberg.moviedbshell.viewmodel.PopularMoviesViewModel
import timber.log.Timber

class PopularMoviesFragment : BaseMovieListFragment<PopularMoviesViewModel, MovieListState>() {

    override val viewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.popularViewModelFactory
    }

//    override val viewModel: PopularMoviesViewModel by fragmentViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        (activity?.application as ZephyrrApplication).component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.text = getString(R.string.popular_row)
    }

    override fun onClickListener(movieId: Int) {
        navigate(
            R.id.action_popularMoviesFragment_to_movieDetailFragment,
            MovieDetailArgs(movieId)
        )
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }

    companion object {
        @JvmField
        val TAG: String = PopularMoviesFragment::class.java.simpleName
    }
}
