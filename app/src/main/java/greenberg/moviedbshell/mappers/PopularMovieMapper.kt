package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.models.popularmoviesmodels.PopularMovieResponse
import javax.inject.Inject

class PopularMovieMapper
@Inject constructor(): Mapper<PopularMovieResponse, List<MovieItem>> {
    override fun mapToEntity(item: PopularMovieResponse): List<MovieItem> {
        val mappedItems = item.results?.map { result ->
            MovieItem(
                    movieTitle = result?.title.orEmpty(),
                    overview = result?.overview.orEmpty(),
                    releaseDate = result?.releaseDate.orEmpty(),
                    posterImageUrl = result?.posterPath.orEmpty(),
                    id = result?.id
            )
        }
        //TODO: is this correct
        return mappedItems ?: emptyList()
    }
}