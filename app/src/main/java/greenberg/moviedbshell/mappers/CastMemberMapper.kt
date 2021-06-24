package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.sharedmodels.CastResponseItem
import greenberg.moviedbshell.models.ui.CastMemberItem
import javax.inject.Inject

class CastMemberMapper
@Inject constructor() : Mapper<List<CastResponseItem?>?, List<CastMemberItem>> {
    override fun mapToEntity(item: List<CastResponseItem?>?): List<CastMemberItem> {
        val mappedItems = item?.map { castResponseItem ->
            CastMemberItem(
                role = castResponseItem?.character.orEmpty(),
                name = castResponseItem?.name.orEmpty(),
                posterUrl = castResponseItem?.profilePath.orEmpty(),
                id = castResponseItem?.id
            )
        }
        return mappedItems ?: emptyList()
    }
}
