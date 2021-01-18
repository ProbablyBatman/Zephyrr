package greenberg.moviedbshell.models.tvdetailmodels

import com.google.gson.annotations.SerializedName
import greenberg.moviedbshell.models.sharedmodels.CrewResponseItem

data class EpisodesItem(
    @field:SerializedName("production_code")
    val productionCode: String? = null,

    @field:SerializedName("air_date")
    val airDate: String? = null,

    @field:SerializedName("overview")
    val overview: String? = null,

    @field:SerializedName("episode_number")
    val episodeNumber: Int? = null,

    @field:SerializedName("vote_average")
    val voteAverage: Double? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("season_number")
    val seasonNumber: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("still_path")
    val stillPath: String? = null,

    @field:SerializedName("vote_count")
    val voteCount: Int? = null,

    @field:SerializedName("crew")
    val crew: List<CrewResponseItem?>? = null,

    @field:SerializedName("guest_stars")
    val guestStars: List<GuestStarsItem?>? = null
)
