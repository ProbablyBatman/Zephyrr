package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.MvRxState
import greenberg.moviedbshell.models.ui.ProductionDetailItem
import kotlinx.parcelize.Parcelize

data class ProductionDetailState(
    val productionDetailItem: ProductionDetailItem
) : MvRxState {
    constructor(args: ProductionDetailStateArgs) : this(productionDetailItem = args.productionDetailItem)
}

@Parcelize
data class ProductionDetailStateArgs(
    val productionDetailItem: ProductionDetailItem
) : Parcelable