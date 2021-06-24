package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.movielistmodels.MovieListResponse
import greenberg.moviedbshell.models.ui.MovieItem
import javax.inject.Inject

class MovieListMapper
@Inject constructor() : Mapper<MovieListResponse, List<MovieItem>> {
    override fun mapToEntity(item: MovieListResponse?): List<MovieItem> {
        val mappedItems = item?.results?.map { result ->
            MovieItem(
                movieTitle = result?.title.orEmpty(),
                overview = result?.overview.orEmpty(),
                releaseDate = result?.releaseDate.orEmpty(),
                posterImageUrl = result?.posterPath.orEmpty(),
                id = result?.id
            )
        }
        return mappedItems.orEmpty()
    }
}
