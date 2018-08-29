package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.sharedmodels.CastResponseItem
import greenberg.moviedbshell.models.ui.CastItem
import javax.inject.Inject

class CastMemberMapper
@Inject constructor() : Mapper<List<CastResponseItem?>?, List<CastItem>> {
    override fun mapToEntity(item: List<CastResponseItem?>?): List<CastItem> {
        val mappedItems = item?.map { castResponseItem ->
            CastItem(
                    role = castResponseItem?.character.orEmpty(),
                    name = castResponseItem?.name.orEmpty(),
                    posterUrl = castResponseItem?.profilePath.orEmpty(),
                    id = castResponseItem?.id
            )
        }
        return mappedItems ?: emptyList()
    }
}