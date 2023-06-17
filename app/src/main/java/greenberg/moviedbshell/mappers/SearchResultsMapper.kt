package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.models.searchmodels.SearchResponse
import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.models.ui.PersonItem
import greenberg.moviedbshell.models.ui.PreviewItem
import greenberg.moviedbshell.models.ui.TvItem
import javax.inject.Inject

class SearchResultsMapper
@Inject constructor(private val knownForMapper: KnownForMapper) : Mapper<SearchResponse, List<PreviewItem>> {
    override fun mapToEntity(item: SearchResponse?): List<PreviewItem> {
        val mappedItems = item?.results?.map { result ->
            // TODO: address this uppercase issue
            when (result?.mediaType?.let { MediaType.valueOf(it.uppercase()) }) {
                MediaType.MOVIE ->
                    MovieItem(
                        movieTitle = result.title.orEmpty(),
                        overview = result.overview.orEmpty(),
                        releaseDate = result.releaseDate.orEmpty(),
                        posterImageUrl = result.posterPath.orEmpty(),
                        id = result.id,
                    )
                MediaType.TV -> {
                    TvItem(
                        name = result.name.orEmpty(),
                        overview = result.overview.orEmpty(),
                        firstAirDate = result.firstAirDate.orEmpty(),
                        posterImageUrl = result.posterPath.orEmpty(),
                        id = result.id,
                    )
                }
                MediaType.PERSON -> {
                    PersonItem(
                        name = result.name.orEmpty(),
                        posterImageUrl = result.profilePath.orEmpty(),
                        knownForItems = knownForMapper.mapToEntity(result.knownFor),
                        id = result.id,
                    )
                }
                else -> PreviewItem(mediaType = MediaType.UNKNOWN)
            }
        }
        return mappedItems ?: emptyList()
    }

    // TODO: remove this because it shouldn't need to exist
    fun mapUnmarkedMovies(item: SearchResponse?): List<PreviewItem> {
        val mappedItems = item?.results?.map { result ->
            MovieItem(
                movieTitle = result?.title.orEmpty(),
                overview = result?.overview.orEmpty(),
                releaseDate = result?.releaseDate.orEmpty(),
                posterImageUrl = result?.posterPath.orEmpty(),
                id = result?.id,
            )
        }
        return mappedItems ?: emptyList()
    }
}
