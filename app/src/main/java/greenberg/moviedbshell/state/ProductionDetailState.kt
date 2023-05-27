package greenberg.moviedbshell.state

import android.os.Parcelable
import greenberg.moviedbshell.models.ui.ProductionDetailItem
import kotlinx.parcelize.Parcelize

data class ProductionDetailState(
    val productionDetailItem: ProductionDetailItem
) {
    constructor(args: ProductionDetailStateArgs) : this(productionDetailItem = args.productionDetailItem)
}

@Parcelize
data class ProductionDetailStateArgs(
    val productionDetailItem: ProductionDetailItem
) : Parcelable
