package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.recentlyreleasedmodels.RecentlyReleasedResponse
import greenberg.moviedbshell.models.ui.MovieItem
import javax.inject.Inject

class RecentlyReleasedMapper
@Inject constructor() : Mapper<RecentlyReleasedResponse, List<MovieItem>> {
    override fun mapToEntity(item: RecentlyReleasedResponse?): List<MovieItem> {
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