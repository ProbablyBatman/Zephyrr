package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.View
import com.airbnb.mvrx.fragmentViewModel
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.base.BaseMovieListFragment
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.MovieListState
import greenberg.moviedbshell.viewmodel.RecentlyReleasedViewModel
import timber.log.Timber

class RecentlyReleasedFragment : BaseMovieListFragment<RecentlyReleasedViewModel, MovieListState>() {

    override val viewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.recentlyReleasedViewModelFactory
    }

//    override val viewModel: RecentlyReleasedViewModel by fragmentViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        (activity?.application as ZephyrrApplication).component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title.text = getString(R.string.recently_released_row)
    }

    override fun onClickListener(movieId: Int) {
        navigate(
            R.id.action_recentlyReleasedFragment_to_movieDetailFragment,
            MovieDetailArgs(movieId)
        )
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }
}
