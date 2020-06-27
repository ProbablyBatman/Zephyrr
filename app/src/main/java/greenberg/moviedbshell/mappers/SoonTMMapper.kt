package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.soontmmodels.SoonTMResponse
import greenberg.moviedbshell.models.ui.MovieItem
import javax.inject.Inject

class SoonTMMapper
@Inject constructor() : Mapper<SoonTMResponse, List<MovieItem>> {
    override fun mapToEntity(item: SoonTMResponse?): List<MovieItem> {
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