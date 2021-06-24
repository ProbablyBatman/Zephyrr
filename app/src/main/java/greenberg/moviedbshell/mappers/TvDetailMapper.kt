package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.container.TvDetailResponseContainer
import greenberg.moviedbshell.models.ui.CrewMemberItem
import greenberg.moviedbshell.models.ui.TvDetailItem
import okhttp3.internal.toImmutableList
import javax.inject.Inject

class TvDetailMapper
@Inject constructor(
    private val castMemberMapper: CastMemberMapper,
    private val crewMemberMapper: CrewMemberMapper,
    private val networksMapper: NetworksMapper,
    private val productionCompanyMapper: ProductionCompanyMapper,
    private val productionCountryMapper: ProductionCountryMapper,
    private val aggregateCastMapper: AggregateCastMapper,
    private val aggregateCrewMapper: AggregateCrewMapper,
    private val posterMapper: PosterGalleryMapper
) : Mapper<TvDetailResponseContainer, TvDetailItem> {
    override fun mapToEntity(item: TvDetailResponseContainer?): TvDetailItem {
        val tvDetail = item?.tvShowResponse
        val creditsDetail = item?.creditsResponse
        // Need to add creator to the crew members list manually
        val crewMembers = crewMemberMapper.mapToEntity(creditsDetail?.crew).toMutableList()
        // Reverse the list so creators are added in whatever order this list provides
        tvDetail?.createdBy?.reversed()?.forEach {
            val creator = CrewMemberItem(
                job = "Creator",
                name = it?.name.orEmpty(),
                posterUrl = it?.profilePath.orEmpty(),
                id = it?.id
            )
            crewMembers.add(0, creator)
        }
        val imageGallery = item?.posterResponse
        val createdByPreviewList = tvDetail?.createdBy?.map {
            CrewMemberItem(
                job = "Created By",
                name = it?.name.orEmpty(),
                posterUrl = it?.profilePath.orEmpty(),
                id = it?.id
            )
        }
        return TvDetailItem(
            title = tvDetail?.name.orEmpty(),
            originalTitle = tvDetail?.originalName.orEmpty(),
            firstAirDate = tvDetail?.firstAirDate.orEmpty(),
            lastAirDate = tvDetail?.lastAirDate.orEmpty(),
            nextAirDate = tvDetail?.nextEpisodeToAir?.airDate.orEmpty(),
            status = tvDetail?.status.orEmpty(),
            numberOfEpisodes = tvDetail?.numberOfEpisodes ?: 0,
            numberOfSeasons = tvDetail?.numberOfSeasons ?: 0,
            voteAverage = tvDetail?.voteAverage ?: 0.0,
            voteCount = tvDetail?.voteCount ?: 0,
            runtime = tvDetail?.episodeRunTime?.map { it ?: 0 }.orEmpty(),
            genres = tvDetail?.genres?.mapNotNull { it?.name }.orEmpty(),
            posterImageUrl = tvDetail?.posterPath.orEmpty(),
            backgroundImageUrl = tvDetail?.backdropPath.orEmpty(),
            overview = tvDetail?.overview.orEmpty(),
            castMembers = castMemberMapper.mapToEntity(creditsDetail?.cast),
            aggregateCastMembers = aggregateCastMapper.mapToEntity(item?.aggregateCreditsResponse?.cast),
            aggregateCrewMembers = aggregateCrewMapper.mapToEntity(item?.aggregateCreditsResponse?.crew),
            crewMembers = crewMembers.toImmutableList(),
            createdBy = createdByPreviewList.orEmpty(),
            networks = networksMapper.mapToEntity(tvDetail?.networks),
            productionCompanies = productionCompanyMapper.mapToEntity(tvDetail?.productionCompanies),
            productionCountries = productionCountryMapper.mapToEntity(tvDetail?.productionCountries),
            posterUrls = posterMapper.mapToEntity(imageGallery)
        )
    }
}
