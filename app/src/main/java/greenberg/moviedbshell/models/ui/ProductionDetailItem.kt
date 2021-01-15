package greenberg.moviedbshell.models.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductionDetailItem(
    val movieTitle: String,
    val originalTitle: String,
    val releaseDate: String,
    val budget: Long,
    val runtime: Int,
    val status: String,
    val revenue: Long,
    val crewMembers: List<CrewMemberItem>,
    val productionCompanies: List<ProductionCompanyItem>,
    val productionCountries: List<ProductionCountryItem>
) : Parcelable
