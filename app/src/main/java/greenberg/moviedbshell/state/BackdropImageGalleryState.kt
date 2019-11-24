package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.models.ui.BackdropPosterItem
import kotlinx.android.parcel.Parcelize

data class BackdropImageGalleryState(
    val itemId: Int = -1,
    val mediaType: String = MediaType.UNKNOWN,
    val backdropItems: List<BackdropPosterItem>? = emptyList(),
    val backdropItemResponse: Async<List<BackdropPosterItem>> = Uninitialized
) : MvRxState {
    constructor(args: BackdropImageGalleryArgs) : this(itemId = args.itemId, mediaType = args.mediaType)
}

@Parcelize
data class BackdropImageGalleryArgs(
    val itemId: Int,
    val mediaType: String
) : Parcelable
