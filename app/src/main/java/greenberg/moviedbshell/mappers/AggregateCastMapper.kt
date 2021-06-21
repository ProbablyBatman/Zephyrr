package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.tvdetailmodels.AggregateCastResponseItem
import greenberg.moviedbshell.models.ui.AggregateCastMemberItem
import javax.inject.Inject

class AggregateCastMapper
@Inject constructor(
    private val rolesMapper: RolesMapper
) : Mapper<List<AggregateCastResponseItem?>?, List<AggregateCastMemberItem>> {
    override fun mapToEntity(item: List<AggregateCastResponseItem?>?): List<AggregateCastMemberItem> {
        val mappedItems = item?.map { castResponseItem ->
            AggregateCastMemberItem(
                name = castResponseItem?.name.orEmpty(),
                originalName = castResponseItem?.originalName.orEmpty(),
                order = castResponseItem?.order ?: -1,
                totalEpisodeCount = castResponseItem?.totalEpisodeCount ?: -1,
                posterUrl = castResponseItem?.profilePath.orEmpty(),
                roles = rolesMapper.mapToEntity(castResponseItem?.roles),
                id = castResponseItem?.id
            )
        }
        return mappedItems ?: emptyList()
    }
}