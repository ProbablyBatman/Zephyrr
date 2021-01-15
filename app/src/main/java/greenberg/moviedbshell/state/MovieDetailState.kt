package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import greenberg.moviedbshell.models.ui.MovieDetailItem
import greenberg.moviedbshell.models.ui.ProductionDetailItem
import kotlinx.android.parcel.Parcelize

data class MovieDetailState(
    val movieId: Int = -1,
    val movieDetailItem: MovieDetailItem? = null,
    val productionDetailItem: ProductionDetailItem? = null,
    val movieDetailResponse: Async<MovieDetailItem> = Uninitialized
) : MvRxState {
    constructor(args: MovieDetailArgs) : this(movieId = args.movieId)
}

@Parcelize
data class MovieDetailArgs(
    val movieId: Int
) : Parcelable