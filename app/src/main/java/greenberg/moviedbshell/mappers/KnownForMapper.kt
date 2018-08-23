package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.searchmodels.KnownForItem
import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.models.ui.PreviewItem
import greenberg.moviedbshell.models.ui.TvItem
import greenberg.moviedbshell.presenters.SearchPresenter
import javax.inject.Inject

class KnownForMapper
@Inject constructor(): Mapper<List<KnownForItem?>?, List<PreviewItem>> {
    override fun mapToEntity(item: List<KnownForItem?>?): List<PreviewItem> {
        val mappedItems = item?.map {
            when (it?.mediaType) {
                SearchPresenter.MEDIA_TYPE_MOVIE ->
                    MovieItem(
                        movieTitle =  it.title.orEmpty(),
                        overview = it.overview.orEmpty(),
                        releaseDate = it.releaseDate.orEmpty(),
                        posterImageUrl = it.posterPath.orEmpty(),
                        id = it.id
                    )
                SearchPresenter.MEDIA_TYPE_TV -> {
                    TvItem(
                        name = it.name.orEmpty(),
                        overview = it.overview.orEmpty(),
                        firstAirDate = it.firstAirDate.orEmpty(),
                        posterImageUrl = it.posterPath.orEmpty(),
                        id = it.id
                    )
                }
                else -> PreviewItem(mediaType = SearchPresenter.MEDIA_TYPE_UNKNOWN)
            }
        }
        return mappedItems ?: emptyList()
    }
}