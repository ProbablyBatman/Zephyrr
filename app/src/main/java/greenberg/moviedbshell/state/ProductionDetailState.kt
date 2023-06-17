package greenberg.moviedbshell.state

import android.os.Parcelable
import greenberg.moviedbshell.models.ui.ProductionDetailItem
import kotlinx.parcelize.Parcelize

data class ProductionDetailState(
    val productionDetailItem: ProductionDetailItem,
)

@Parcelize
data class ProductionDetailStateArgs(
    val productionDetailItem: ProductionDetailItem,
) : Parcelable
