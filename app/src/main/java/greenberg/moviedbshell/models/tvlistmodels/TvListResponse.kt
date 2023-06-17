package greenberg.moviedbshell.models.tvlistmodels

import com.google.gson.annotations.SerializedName

data class TvListResponse(
    @field:SerializedName("page")
    val page: Int? = null,

    @field:SerializedName("total_pages")
    val totalPages: Int? = null,

    @field:SerializedName("results")
    val results: List<TvListResponseItem?>? = null,

    @field:SerializedName("total_results")
    val totalResults: Int? = null,
)
