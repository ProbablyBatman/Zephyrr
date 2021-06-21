package greenberg.moviedbshell.models.ui

import kotlinx.parcelize.Parcelize

@Parcelize
data class TvDetailItem(
    val title: String,
    val originalTitle: String,
    val status: String,
    val firstAirDate: String,
    val lastAirDate: String,
    val nextAirDate: String,
    val numberOfEpisodes: Int,
    val numberOfSeasons: Int,
    val voteAverage: Double,
    val voteCount: Int,
    val runtime: List<Int>,
    val genres: List<String>,
    val posterImageUrl: String,
    val backgroundImageUrl: String,
    val overview: String,
    val castMembers: List<CastMemberItem>,
    val aggregateCastMembers: List<AggregateCastMemberItem>,
    val aggregateCrewMembers: List<AggregateCrewMemberItem>,
    val networks: List<NetworkItem>,
    val posterUrls: List<PosterItem>,
    val createdBy: List<CrewMemberItem>,
    val crewMembers: List<CrewMemberItem>,
    val productionCompanies: List<ProductionCompanyItem>,
    val productionCountries: List<ProductionCountryItem>
) : ProductionDetailItem