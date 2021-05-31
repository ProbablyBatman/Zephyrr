package greenberg.moviedbshell.models.ui

import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieDetailItem(
    val movieId: Int,
    val title: String,
    val originalTitle: String,
    val status: String,
    val releaseDate: String,
    val budget: Long,
    val runtime: Int,
    val revenue: Long,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<String>,
    val posterImageUrl: String,
    val backdropImageUrl: String,
    val overview: String,
    val castMembers: List<CastMemberItem>,
    val posterUrls: List<PosterItem>,
    val crewMembers: List<CrewMemberItem>,
    val productionCompanies: List<ProductionCompanyItem>,
    val productionCountries: List<ProductionCountryItem>
) : ProductionDetailItem