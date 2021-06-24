package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.MavericksState
import greenberg.moviedbshell.models.ui.ProductionDetailItem
import kotlinx.parcelize.Parcelize

data class ProductionDetailState(
    val productionDetailItem: ProductionDetailItem
) : MavericksState {
    constructor(args: ProductionDetailStateArgs) : this(productionDetailItem = args.productionDetailItem)
}

@Parcelize
data class ProductionDetailStateArgs(
    val productionDetailItem: ProductionDetailItem
) : Parcelable
