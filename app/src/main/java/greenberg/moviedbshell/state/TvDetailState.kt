package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import greenberg.moviedbshell.models.ui.TvDetailItem
import kotlinx.android.parcel.Parcelize

data class TvDetailState(
    val tvId: Int = -1,
    val tvDetailItem: TvDetailItem? = null,
    val tvDetailResponse: Async<TvDetailItem> = Uninitialized
) : MvRxState {
    constructor(args: TvDetailArgs) : this(tvId = args.tvId)
}

@Parcelize
data class TvDetailArgs(
    val tvId: Int
) : Parcelable
