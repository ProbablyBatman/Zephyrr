package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.tvlistmodels.TvListResponse
import greenberg.moviedbshell.models.ui.TvItem
import javax.inject.Inject

class TvListMapper
@Inject()
constructor() : Mapper<TvListResponse, List<TvItem>> {
    override fun mapToEntity(item: TvListResponse?): List<TvItem> {
        val mappedItems = item?.results?.map { result ->
            TvItem(
                name = result?.name.orEmpty(),
                overview = result?.overview.orEmpty(),
                firstAirDate = result?.firstAirDate.orEmpty(),
                posterImageUrl = result?.posterPath.orEmpty(),
                id = result?.id,
            )
        }
        return mappedItems.orEmpty()
    }
}
