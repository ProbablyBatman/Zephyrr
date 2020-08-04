package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.BackdropGalleryMapper
import greenberg.moviedbshell.mappers.PosterGalleryMapper
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.ImageGalleryState
import greenberg.moviedbshell.view.ImageGalleryDialog
import io.reactivex.schedulers.Schedulers

class ImageGalleryViewModel
@AssistedInject constructor(
    @Assisted var state: ImageGalleryState,
    private val TMDBService: TMDBService,
    private val mapper: PosterGalleryMapper,
    private val backdropMapper: BackdropGalleryMapper
) : ZephyrrMvRxViewModel<ImageGalleryState>(state) {

    @AssistedInject.Factory
    interface Factory {
        fun create(state: ImageGalleryState): ImageGalleryViewModel
    }

    init {
        logStateChanges()
        fetchPosters()
    }

    fun fetchPosters() {
        withState { state ->
            if (state.imageGalleryResponse is Loading) return@withState
            if (state.mediaType == MediaType.MOVIE) {
                TMDBService
                    .queryMovieImages(state.itemId)
                    .subscribeOn(Schedulers.io())
                    .execute {
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
    }

    companion object : MvRxViewModelFactory<ImageGalleryViewModel, ImageGalleryState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: ImageGalleryState): ImageGalleryViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<ImageGalleryDialog>().imageGalleryViewModelFactory
            return fragment.create(state)
        }
    }
}