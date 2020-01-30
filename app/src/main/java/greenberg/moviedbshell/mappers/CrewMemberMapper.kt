package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.sharedmodels.CrewResponseItem
import greenberg.moviedbshell.models.ui.CrewMemberItem
import javax.inject.Inject

class CrewMemberMapper
@Inject constructor() : Mapper<List<CrewResponseItem?>?, List<CrewMemberItem>> {
    override fun mapToEntity(item: List<CrewResponseItem?>?): List<CrewMemberItem> {
        val mappedItems = item?.map { crewResponseItem ->
            CrewMemberItem(
                    job = crewResponseItem?.job.orEmpty(),
                    name = crewResponseItem?.name.orEmpty(),
                    posterUrl = crewResponseItem?.profilePath.orEmpty(),
                    id = crewResponseItem?.id
            )
        }
        return mappedItems ?: emptyList()
    }
}