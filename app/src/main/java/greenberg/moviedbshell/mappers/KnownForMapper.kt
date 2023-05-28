package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.models.searchmodels.KnownForItem
import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.models.ui.PreviewItem
import greenberg.moviedbshell.models.ui.TvItem
import javax.inject.Inject

class KnownForMapper
@Inject constructor() : Mapper<List<KnownForItem?>?, List<PreviewItem>> {
    override fun mapToEntity(item: List<KnownForItem?>?): List<PreviewItem> {
        val mappedItems = item?.map { knownForItem ->
            when (knownForItem?.mediaType?.let { MediaType.valueOf(it) }) {
                MediaType.MOVIE ->
                    MovieItem(
                        movieTitle = knownForItem.title.orEmpty(),
                        overview = knownForItem.overview.orEmpty(),
                        releaseDate = knownForItem.releaseDate.orEmpty(),
                        posterImageUrl = knownForItem.posterPath.orEmpty(),
                        id = knownForItem.id
                    )
                MediaType.TV -> {
                    TvItem(
                        name = knownForItem.name.orEmpty(),
                        overview = knownForItem.overview.orEmpty(),
                        firstAirDate = knownForItem.firstAirDate.orEmpty(),
                        posterImageUrl = knownForItem.posterPath.orEmpty(),
                        id = knownForItem.id
                    )
                }
                else -> PreviewItem(mediaType = MediaType.UNKNOWN)
            }
        }
        return mappedItems ?: emptyList()
    }
}
