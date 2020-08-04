package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.models.imagegallerymodels.ImageGalleryResponse
import greenberg.moviedbshell.models.ui.PosterItem
import kotlinx.android.parcel.Parcelize

data class ImageGalleryState(
    val itemId: Int = -1,
    val mediaType: String = MediaType.UNKNOWN,
    val posterItems: List<PosterItem> = emptyList(),
    val backdropItems: List<PosterItem> = emptyList(),
    val imageGalleryResponse: Async<ImageGalleryResponse> = Uninitialized
) : MvRxState {
    constructor(args: PosterImageGalleryArgs) : this(itemId = args.itemId, mediaType = args.mediaType)
}

@Parcelize
data class PosterImageGalleryArgs(
    val itemId: Int,
    val mediaType: String
) : Parcelable