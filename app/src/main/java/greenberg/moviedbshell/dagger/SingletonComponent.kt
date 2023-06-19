package greenberg.moviedbshell.dagger

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import greenberg.moviedbshell.view.PopularMoviesFragment
import greenberg.moviedbshell.view.RecentlyReleasedFragment
import greenberg.moviedbshell.view.SoonTMFragment
import greenberg.moviedbshell.viewmodel.ImageGalleryViewModel
import greenberg.moviedbshell.viewmodel.LandingViewModel
import greenberg.moviedbshell.viewmodel.MovieDetailViewModel
import greenberg.moviedbshell.viewmodel.PersonDetailViewModel
import greenberg.moviedbshell.viewmodel.PopularMoviesViewModel
import greenberg.moviedbshell.viewmodel.RecentlyReleasedViewModel
import greenberg.moviedbshell.viewmodel.SearchResultsViewModel
import greenberg.moviedbshell.viewmodel.SoonTMViewModel
import greenberg.moviedbshell.viewmodel.TvDetailViewModel

@InstallIn(SingletonComponent::class)
@EntryPoint
interface SingletonComponent {
//    val popularViewModelFactory: PopularMoviesViewModel.Factory
//    val movieDetailViewModelFactory: MovieDetailViewModel.Factory
//    val personDetailViewModelFactory: PersonDetailViewModel.Factory
//    val tvDetailViewModelFactory: TvDetailViewModel.Factory
//    val searchResultsViewModelFactory: SearchResultsViewModel.Factory
//    val landingViewModelFactory: LandingViewModel.Factory
//    val recentlyReleasedViewModelFactory: RecentlyReleasedViewModel.Factory
//    val soonTMViewModelFactory: SoonTMViewModel.Factory
//    val imageGalleryViewModelFactory: ImageGalleryViewModel.Factory
//    fun inject(fragment: RecentlyReleasedFragment)
//    fun inject(fragment: SoonTMFragment)
//    fun inject(fragment: PopularMoviesFragment)
}
