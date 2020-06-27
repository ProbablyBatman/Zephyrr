package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.container.MovieDetailResponseContainer
import greenberg.moviedbshell.models.ui.MovieDetailItem
import javax.inject.Inject

class MovieDetailMapper
@Inject constructor(
    private val castMemberMapper: CastMemberMapper,
    private val crewMemberMapper: CrewMemberMapper
) : Mapper<MovieDetailResponseContainer, MovieDetailItem> {
    override fun mapToEntity(item: MovieDetailResponseContainer?): MovieDetailItem {
        val movieDetail = item?.movieDetailResponse
        val creditsDetail = item?.creditsResponse
        return MovieDetailItem(
                movieId = movieDetail?.id ?: 0,
                movieTitle = movieDetail?.title.orEmpty(),
                originalTitle = movieDetail?.originalTitle.orEmpty(),
                releaseDate = movieDetail?.releaseDate.orEmpty(),
                voteAverage = movieDetail?.voteAverage ?: 0.0,
                voteCount = movieDetail?.voteCount ?: 0,
                budget = movieDetail?.budget ?: 0L,
                runtime = movieDetail?.runtime ?: 0,
                status = movieDetail?.status.orEmpty(),
                genres = movieDetail?.genres?.mapNotNull { it?.name }.orEmpty(),
                posterImageUrl = movieDetail?.posterPath.orEmpty(),
                backdropImageUrl = movieDetail?.backdropPath.orEmpty(),
                overview = movieDetail?.overview.orEmpty(),
                castMembers = castMemberMapper.mapToEntity(creditsDetail?.cast),
                crewMembers = crewMemberMapper.mapToEntity(creditsDetail?.crew)
        )
    }
}