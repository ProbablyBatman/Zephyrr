package greenberg.moviedbshell.state

import android.os.Parcelable
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.models.imagegallerymodels.ImageGalleryResponse
import greenberg.moviedbshell.models.ui.PosterItem
import kotlinx.parcelize.Parcelize

data class ImageGalleryState(
    val itemId: Int = -1,
    val mediaType: MediaType = MediaType.UNKNOWN,
    val posterItems: List<PosterItem> = emptyList(),
    val backdropItems: List<PosterItem> = emptyList(),
    val error: Throwable? = null,
    val isLoading: Boolean = true,
)

@Parcelize
data class PosterImageGalleryArgs(
    val itemId: Int,
    val mediaType: MediaType
) : Parcelable
