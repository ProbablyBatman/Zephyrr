package greenberg.moviedbshell.state

import android.os.Parcelable
import greenberg.moviedbshell.models.ui.MovieDetailItem
import kotlinx.parcelize.Parcelize

data class MovieDetailState(
    val movieId: Int = -1,
    val movieDetailItem: MovieDetailItem? = null,
//    val movieDetailResponse: Async<MovieDetailItem> = Uninitialized
    val movieDetailResponse: Any
) {
//    constructor(args: MovieDetailArgs) : this(movieId = args.movieId)
}

@Parcelize
data class MovieDetailArgs(
    val movieId: Int
) : Parcelable
