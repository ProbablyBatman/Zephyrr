package greenberg.moviedbshell.models.tvdetailmodels

import com.google.gson.annotations.SerializedName

data class TvSeasonDetailResponse(
    @field:SerializedName("air_date")
    val airDate: String? = null,

    @field:SerializedName("overview")
    val overview: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("season_number")
    val seasonNumber: Int? = null,

    @field:SerializedName("_id")
    val _id: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("episodes")
    val episodes: List<EpisodesItem?>? = null,

    @field:SerializedName("poster_path")
    val posterPath: String? = null,
)
