package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.moviedetailmodels.MovieDetailResponse
import greenberg.moviedbshell.models.sharedmodels.CreditsResponse
import greenberg.moviedbshell.models.ui.MovieDetailItem
import javax.inject.Inject

class MovieDetailMapper
@Inject constructor(private val castMemberMapper: CastMemberMapper) : Mapper<Pair<MovieDetailResponse, CreditsResponse>, MovieDetailItem> {
    override fun mapToEntity(item: Pair<MovieDetailResponse, CreditsResponse>?): MovieDetailItem {
        val movieDetail = item?.first
        val creditsDetail = item?.second
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
                castMembers = castMemberMapper.mapToEntity(creditsDetail?.cast)
        )
    }
}