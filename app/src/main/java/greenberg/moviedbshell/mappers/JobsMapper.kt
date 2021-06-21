package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.tvdetailmodels.AggregateJobsResponseItem
import greenberg.moviedbshell.models.ui.JobItem
import javax.inject.Inject

class JobsMapper
@Inject constructor() : Mapper<List<AggregateJobsResponseItem?>?, List<JobItem>> {
    override fun mapToEntity(item: List<AggregateJobsResponseItem?>?): List<JobItem> {
        val mappedItems = item?.map {
            JobItem(
                job = it?.job.orEmpty(),
                episodeCount = it?.episodeCount ?: -1
            )
        }
        // Sort so highest number of episode role is always first
        return mappedItems?.sortedByDescending { it.episodeCount } ?: emptyList()
    }
}