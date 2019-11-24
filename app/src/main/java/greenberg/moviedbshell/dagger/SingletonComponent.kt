package greenberg.moviedbshell.dagger

import dagger.Component
import greenberg.moviedbshell.viewmodel.BackdropImageGalleryViewModel
import greenberg.moviedbshell.viewmodel.MovieDetailViewModel
import greenberg.moviedbshell.viewmodel.PersonDetailViewModel
import greenberg.moviedbshell.viewmodel.PopularMoviesViewModel
import greenberg.moviedbshell.viewmodel.SearchResultsViewModel
import greenberg.moviedbshell.viewmodel.TvDetailViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [(ApplicationModule::class), (ServicesModule::class), (AssistedInjectModule::class)])
interface SingletonComponent {
    val popularViewModelFactory: PopularMoviesViewModel.Factory
    val movieDetailViewModelFactory: MovieDetailViewModel.Factory
    val personDetailViewModelFactory: PersonDetailViewModel.Factory
    val tvDetailViewModelFactory: TvDetailViewModel.Factory
    val searchResultsViewModelFactory: SearchResultsViewModel.Factory
    val backdropImageGalleryViewModelFactory: BackdropImageGalleryViewModel.Factory
}