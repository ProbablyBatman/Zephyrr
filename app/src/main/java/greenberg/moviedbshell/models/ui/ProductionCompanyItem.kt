package greenberg.moviedbshell.models.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductionCompanyItem(
    val logoPath: String,
    val name: String,
    val originCountry: String,
) : Parcelable {
    companion object {
        fun generateDummy() = ProductionCompanyItem("", "Unknown", "")
    }
}
