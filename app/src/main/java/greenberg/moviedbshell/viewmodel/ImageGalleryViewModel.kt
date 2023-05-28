package greenberg.moviedbshell.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import greenberg.moviedbshell.base.ZephyrrResponse
import greenberg.moviedbshell.repository.TmdbRepository
import greenberg.moviedbshell.mappers.BackdropGalleryMapper
import greenberg.moviedbshell.mappers.PosterGalleryMapper
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.state.ImageGalleryState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class ImageGalleryViewModel
@AssistedInject constructor(
    @Assisted private val itemId: Int,
    @Assisted private val mediaType: MediaType,
    @Assisted private val dispatcher: CoroutineDispatcher,
    private val tmdbRepository: TmdbRepository,
    private val posterMapper: PosterGalleryMapper,
    private val backdropMapper: BackdropGalleryMapper
) : ViewModel() {

    private val _imageGalleryState = MutableStateFlow(
        ImageGalleryState(
            itemId = itemId,
            mediaType = mediaType,
        )
    )
    val imageGalleryState: StateFlow<ImageGalleryState> = _imageGalleryState.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(
            itemId: Int,
            mediaType: MediaType,
            dispatcher: CoroutineDispatcher
        ): ImageGalleryViewModel
    }

    init {
        fetchPosters()
    }

    // TODO: Investigate no-op here?
    fun fetchPosters() {
        when (mediaType) {
            MediaType.MOVIE -> fetchMovieImages()
            MediaType.TV -> fetchTvImages()
            MediaType.PERSON, MediaType.UNKNOWN -> {
                // no-op
            }
        }
    }

    private fun fetchMovieImages() {
        viewModelScope.launch(dispatcher) {
            Timber.d("launching fetchMovieImages")
            when (val response = tmdbRepository.fetchMovieImages(itemId)) {
                is ZephyrrResponse.Success -> {
                    _imageGalleryState.emit(_imageGalleryState.value.copy(
                        posterItems = posterMapper.mapToEntity(response.value),
                        backdropItems = backdropMapper.mapToEntity(response.value),
                        isLoading = false,
                    ))
                }
                is ZephyrrResponse.Failure -> {
                    _imageGalleryState.emit(_imageGalleryState.value.copy(
                        posterItems = emptyList(),
                        backdropItems = emptyList(),
                        error = response.throwable,
                        isLoading = false,
                    ))
                }
            }
        }
    }

    private fun fetchTvImages() {
        viewModelScope.launch(dispatcher) {
            Timber.d("launching fetchTvImages")
            when (val response = tmdbRepository.fetchTvImages(itemId)) {
                is ZephyrrResponse.Success -> {
                    _imageGalleryState.emit(_imageGalleryState.value.copy(
                        posterItems = posterMapper.mapToEntity(response.value),
                        backdropItems = backdropMapper.mapToEntity(response.value),
                        isLoading = false,
                    ))
                }
                is ZephyrrResponse.Failure -> {
                    _imageGalleryState.emit(_imageGalleryState.value.copy(
                        posterItems = emptyList(),
                        backdropItems = emptyList(),
                        error = response.throwable,
                        isLoading = false,
                    ))
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            itemId: Int,
            mediaType: MediaType,
            dispatcher: CoroutineDispatcher
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(itemId, mediaType, dispatcher) as T
            }
        }
    }
}
