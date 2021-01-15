package greenberg.moviedbshell.models.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductionCountryItem(
    val name: String
) : Parcelable {
    companion object {
        fun generateDummy() = ProductionCountryItem("Unknown")
    }
}