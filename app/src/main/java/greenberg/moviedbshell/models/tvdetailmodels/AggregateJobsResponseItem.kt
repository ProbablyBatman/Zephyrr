package greenberg.moviedbshell.models.tvdetailmodels

import com.google.gson.annotations.SerializedName

data class AggregateJobsResponseItem(
    @field:SerializedName("episode_count")
    val episodeCount: Int? = null,

    @field:SerializedName("credit_id")
    val creditId: String? = null,

    @field:SerializedName("job")
    val job: String? = null,
)
