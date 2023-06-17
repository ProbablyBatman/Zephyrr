package greenberg.moviedbshell.models.tvdetailmodels

import com.google.gson.annotations.SerializedName

data class AggregateRolesResponseItem(
    @field:SerializedName("character")
    val character: String? = null,

    @field:SerializedName("episode_count")
    val episodeCount: Int? = null,

    @field:SerializedName("credit_id")
    val creditId: String? = null,
)
