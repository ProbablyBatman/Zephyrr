package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.searchmodels.SearchResponse
import greenberg.moviedbshell.models.ui.*
import greenberg.moviedbshell.presenters.SearchPresenter
import javax.inject.Inject

class SearchResultsMapper
@Inject constructor(private val knownForMapper: KnownForMapper) : Mapper<SearchResponse, List<PreviewItem>> {
    override fun mapToEntity(item: SearchResponse): List<PreviewItem> {
        val mappedItems = item.results?.map { result ->
            when (result?.mediaType) {
                SearchPresenter.MEDIA_TYPE_MOVIE ->
                    MovieItem(
                        movieTitle = result.title.orEmpty(),
                        overview = result.overview.orEmpty(),
                        releaseDate = result.releaseDate.orEmpty(),
                        posterImageUrl = result.posterPath.orEmpty(),
                        id = result.id
                    )
                SearchPresenter.MEDIA_TYPE_TV -> {
                    TvItem(
                        name = result.name.orEmpty(),
                        overview = result.overview.orEmpty(),
                        firstAirDate = result.firstAirDate.orEmpty(),
                        posterImageUrl = result.posterPath.orEmpty(),
                        id = result.id
                    )
                }
                SearchPresenter.MEDIA_TYPE_PERSON -> {
                    PersonItem(
                        name = result.name.orEmpty(),
                        posterImageUrl = result.posterPath.orEmpty(),
                        knownForItems = knownForMapper.mapToEntity(result.knownFor),
                        id = result.id
                    )
                }
                else -> PreviewItem(mediaType = SearchPresenter.MEDIA_TYPE_UNKNOWN)
            }
        }
        return mappedItems ?: emptyList()
    }
}