package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.moviedetailmodels.MovieDetailResponse
import greenberg.moviedbshell.models.ui.MovieDetailItem
import javax.inject.Inject

class MovieDetailMapper
@Inject constructor() : Mapper<MovieDetailResponse, MovieDetailItem> {
    override fun mapToEntity(item: MovieDetailResponse): MovieDetailItem {
        return MovieDetailItem(
                movieTitle = item.title.orEmpty(),
                originalTitle = item.originalTitle.orEmpty(),
                releaseDate = item.releaseDate.orEmpty(),
                voteAverage = item.voteAverage ?: 0.0,
                voteCount = item.voteCount ?: 0,
                budget = item.budget ?: 0L,
                runtime = item.runtime ?: 0,
                status = item.status.orEmpty(),
                genres = item.genres?.map { it?.name }.orEmpty(),
                posterImageUrl = item.posterPath.orEmpty(),
                backdropImageUrl = item.backdropPath.orEmpty(),
                overview = item.overview.orEmpty()
        )
    }
}