package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.tvdetailmodels.AggregateCrewResponseItem
import greenberg.moviedbshell.models.ui.AggregateCrewMemberItem
import javax.inject.Inject

class AggregateCrewMapper
@Inject constructor(
    private val jobsMapper: JobsMapper
) : Mapper<List<AggregateCrewResponseItem?>?, List<AggregateCrewMemberItem>> {
    override fun mapToEntity(item: List<AggregateCrewResponseItem?>?): List<AggregateCrewMemberItem> {
        val mappedItems = item?.map { crewResponseItem ->
            AggregateCrewMemberItem(
                name = crewResponseItem?.name.orEmpty(),
                originalName = crewResponseItem?.originalName.orEmpty(),
                totalEpisodeCount = crewResponseItem?.totalEpisodeCount ?: -1,
                posterUrl = crewResponseItem?.profilePath.orEmpty(),
                jobs = jobsMapper.mapToEntity(crewResponseItem?.jobs),
                id = crewResponseItem?.id
            )
        }
        return mappedItems ?: emptyList()
    }
}
