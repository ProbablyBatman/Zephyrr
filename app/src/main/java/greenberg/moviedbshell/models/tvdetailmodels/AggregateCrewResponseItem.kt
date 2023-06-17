package greenberg.moviedbshell.models.tvdetailmodels

import com.google.gson.annotations.SerializedName

data class AggregateCrewResponseItem(
    @field:SerializedName("total_episode_count")
    val totalEpisodeCount: Int? = null,

    @field:SerializedName("gender")
    val gender: Int? = null,

    @field:SerializedName("known_for_department")
    val knownForDepartment: String? = null,

    @field:SerializedName("original_name")
    val originalName: String? = null,

    @field:SerializedName("popularity")
    val popularity: Double? = null,

    @field:SerializedName("jobs")
    val jobs: List<AggregateJobsResponseItem?>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("profile_path")
    val profilePath: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("adult")
    val adult: Boolean? = null,

    @field:SerializedName("department")
    val department: String? = null,
)
