package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.tvdetailmodels.AggregateRolesResponseItem
import greenberg.moviedbshell.models.ui.RoleItem
import javax.inject.Inject

class RolesMapper
@Inject constructor() : Mapper<List<AggregateRolesResponseItem?>?, List<RoleItem>> {
    override fun mapToEntity(item: List<AggregateRolesResponseItem?>?): List<RoleItem> {
        val mappedItems = item?.map {
            RoleItem(
                character = it?.character.orEmpty(),
                episodeCount = it?.episodeCount ?: -1,
            )
        }
        // Sort so highest number of episode role is always first
        return mappedItems?.sortedByDescending { it.episodeCount } ?: emptyList()
    }
}
