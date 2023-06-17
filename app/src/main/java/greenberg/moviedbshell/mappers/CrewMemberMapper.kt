package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.sharedmodels.CrewResponseItem
import greenberg.moviedbshell.models.ui.CollapsedCrewMemberItem
import greenberg.moviedbshell.models.ui.CrewMemberItem
import timber.log.Timber
import javax.inject.Inject

class CrewMemberMapper
@Inject constructor() : Mapper<List<CrewResponseItem?>?, List<CrewMemberItem>> {
    override fun mapToEntity(item: List<CrewResponseItem?>?): List<CrewMemberItem> {
        val mappedItems = item?.map { crewResponseItem ->
            CrewMemberItem(
                job = crewResponseItem?.job.orEmpty(),
                name = crewResponseItem?.name.orEmpty(),
                posterUrl = crewResponseItem?.profilePath.orEmpty(),
                id = crewResponseItem?.id,
            )
        }
        // TODO: investigate the actual feasibility of this. My only real use case is a Tarantino movie
        // where he writes, directs, and produces it.
        val collapsedItems: MutableList<CollapsedCrewMemberItem> = mutableListOf()
        mappedItems?.forEach { crewItem ->
            // First find out if person is already in the collapsed list of crew members
            val existingCrewMemberItem = collapsedItems.find { it.name == crewItem.name }
            // If it's not, then add all their jobs
            if (existingCrewMemberItem == null) {
                // Find all the jobs this person had associated with their name
                val filteredItemsByName = mappedItems.filter { it.name == crewItem.name }
                val newCrewMemberItem =
                    CollapsedCrewMemberItem(
                        crewItem.job,
                        crewItem.name,
                        crewItem.posterUrl,
                        crewItem.id,
                    )
                // Append them to the new list
                filteredItemsByName.forEach {
                    if (!newCrewMemberItem.job.contains(it.job)) {
                        newCrewMemberItem.job += ", ${it.job}"
                    }
                }
                collapsedItems.add(newCrewMemberItem)
            }
        }
        Timber.d("sag collapsed is $collapsedItems")
        return mappedItems ?: emptyList()
    }
}
