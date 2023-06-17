package greenberg.moviedbshell.state

import android.os.Parcelable
import greenberg.moviedbshell.models.ui.MovieDetailItem
import kotlinx.parcelize.Parcelize

data class MovieDetailState(
    val movieId: Int = -1,
    val movieDetailItem: MovieDetailItem? = null,
    val error: Throwable? = null,
    val isLoading: Boolean = true,
)

@Parcelize
data class MovieDetailArgs(
    val movieId: Int,
) : Parcelable
