package greenberg.moviedbshell.viewmodel

import com.airbnb.mvrx.FragmentViewModelContext
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrMvRxViewModel
import greenberg.moviedbshell.mappers.BackdropImageMapper
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.services.TMDBService
import greenberg.moviedbshell.state.BackdropImageGalleryState
import greenberg.moviedbshell.view.BackdropImageGalleryDialog
import io.reactivex.schedulers.Schedulers

class BackdropImageGalleryViewModel
@AssistedInject constructor(
    @Assisted var state: BackdropImageGalleryState,
    private val TMDBService: TMDBService,
    private val mapper: BackdropImageMapper
) : ZephyrrMvRxViewModel<BackdropImageGalleryState>(state) {

    @AssistedInject.Factory
    interface Factory {
        fun create(state: BackdropImageGalleryState): BackdropImageGalleryViewModel
    }

    init {
        logStateChanges()
    }

    fun fetchBackdropPosters() {
        withState { state ->
            if (state.backdropItemResponse is Loading) return@withState
            if (state.mediaType == MediaType.MOVIE) {
                TMDBService
                        .queryMovieImages(state.itemId)
                        .map { mapper.mapToEntity(it) }
                        .subscribeOn(Schedulers.io())
                        .execute {
                            // Assume success for now
                            copy(
                                    itemId = state.itemId,
                                    mediaType = state.mediaType,
                                    backdropItems = it(),
                                    backdropItemResponse = it
                            )
                        }
            }
        }
    }

    companion object : MvRxViewModelFactory<BackdropImageGalleryViewModel, BackdropImageGalleryState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: BackdropImageGalleryState): BackdropImageGalleryViewModel {
            val fragment = (viewModelContext as FragmentViewModelContext).fragment<BackdropImageGalleryDialog>().backdropImageGalleryViewModelFactory
            return fragment.create(state)
        }
    }
}