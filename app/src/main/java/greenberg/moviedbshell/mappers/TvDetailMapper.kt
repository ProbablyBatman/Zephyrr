package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.container.TvDetailResponseContainer
import greenberg.moviedbshell.models.ui.TvDetailItem
import javax.inject.Inject

class TvDetailMapper
@Inject constructor(private val castMemberMapper: CastMemberMapper) : Mapper<TvDetailResponseContainer, TvDetailItem> {
    override fun mapToEntity(item: TvDetailResponseContainer?): TvDetailItem {
        val tvDetail = item?.tvDetailResponse
        val creditsDetail = item?.creditsResponse
        return TvDetailItem(
                title = tvDetail?.name.orEmpty(),
                originalTitle = tvDetail?.originalName.orEmpty(),
                firstAirDate = tvDetail?.firstAirDate.orEmpty(),
                lastAirDate = tvDetail?.lastAirDate,
                nextAirDate = tvDetail?.nextEpisodeToAir?.airDate,
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
                castMembers = castMemberMapper.mapToEntity(creditsDetail?.cast)
        )
    }
}