package greenberg.moviedbshell.state

import android.os.Parcelable
import greenberg.moviedbshell.models.ui.TvDetailItem
import kotlinx.parcelize.Parcelize

data class TvDetailState(
    val tvId: Int = -1,
    val tvDetailItem: TvDetailItem? = null,
    val error: Throwable? = null,
    val isLoading: Boolean = true,
)

@Parcelize
data class TvDetailArgs(
    val tvId: Int,
) : Parcelable
