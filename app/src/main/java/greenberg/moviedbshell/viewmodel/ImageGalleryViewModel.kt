package greenberg.moviedbshell.viewmodel

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.mappers.BackdropGalleryMapper
import greenberg.moviedbshell.mappers.PosterGalleryMapper
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.state.ImageGalleryState
import greenberg.moviedbshell.view.ImageGalleryDialog
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ImageGalleryViewModel
@AssistedInject constructor(
    @Assisted var state: ImageGalleryState,
    private val tmdbRepository: TmdbRepository,
    private val mapper: PosterGalleryMapper,
    private val backdropMapper: BackdropGalleryMapper
) : ZephyrrMvRxViewModel<ImageGalleryState>(state) {

    @AssistedFactory
    interface Factory {
        fun create(state: ImageGalleryState): ImageGalleryViewModel
    }

    init {
        fetchPosters()
    }

    fun fetchPosters() {
        withState { state ->
            if (state.imageGalleryResponse is Loading) return@withState
            when (state.mediaType) {
                MediaType.MOVIE -> {
                    fetchMovieImages()
                }
                MediaType.TV -> {
                    fetchTvImages()
                }
            }
        }
    }

    private fun fetchMovieImages(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        withState { state ->
            suspend { tmdbRepository.fetchMovieImages(state.itemId) }
                .execute(dispatcher) {
                    // Assume success for now
                    copy(
                        itemId = state.itemId,
                        mediaType = state.mediaType,
                        posterItems = mapper.mapToEntity(it()),
                        backdropItems = backdropMapper.mapToEntity(it()),
                        imageGalleryResponse = it
                    )
                }
        }
    }

    private fun fetchTvImages(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
        withState { state ->
            suspend { tmdbRepository.fetchTvImages(state.itemId) }
                .execute(dispatcher) {
                    // Assume success for now
                    copy(
                        itemId = state.itemId,
                        mediaType = state.mediaType,
                        posterItems = mapper.mapToEntity(it()),
                        backdropItems = backdropMapper.mapToEntity(it()),
                        imageGalleryResponse = it
                    )
                }
        }
    }

    companion object : MavericksViewModelFactory<ImageGalleryViewModel, ImageGalleryState> {
        override fun create(viewModelContext: ViewModelContext, state: ImageGalleryState): ImageGalleryViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<ImageGalleryDialog>().imageGalleryViewModelFactory
            return fragment.create(state)
        }
    }
}
