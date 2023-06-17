package greenberg.moviedbshell.models.ui

import kotlinx.parcelize.Parcelize

@Parcelize
class AggregateCrewMemberItem(
    val originalName: String,
    val totalEpisodeCount: Int,
    val jobs: List<JobItem>,
    override val name: String,
    override val posterUrl: String,
    override val id: Int?,
) : CrewMemberItem(
    jobs[0].job,
    name,
    posterUrl,
    id,
)
