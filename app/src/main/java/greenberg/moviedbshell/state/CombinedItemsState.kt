package greenberg.moviedbshell.state

import android.os.Parcelable
import greenberg.moviedbshell.models.ui.PreviewItem
import kotlinx.parcelize.Parcelize

// TODO: separate page numbers because results might not be equal
data class CombinedItemsState(
    val moviePageNumber: Int = 1,
    val tvPageNumber: Int = 1,
    val isMovieMaxPages: Boolean = false,
    val isTvMaxPages: Boolean = false,
    val combinedItemList: List<PreviewItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: Throwable? = null,
)

@Parcelize
data class CombinedItemsArgs(
    val ids: List<Int>,
) : Parcelable
