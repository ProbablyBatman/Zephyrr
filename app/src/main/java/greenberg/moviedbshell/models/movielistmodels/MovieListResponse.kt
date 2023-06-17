package greenberg.moviedbshell.models.movielistmodels

import com.google.gson.annotations.SerializedName
import greenberg.moviedbshell.models.base.Dates

data class MovieListResponse(
    @field:SerializedName("dates")
    val dates: Dates? = null,

    @field:SerializedName("page")
    val page: Int? = null,

    @field:SerializedName("total_pages")
    val totalPages: Int? = null,

    @field:SerializedName("results")
    val results: List<MovieListResponseItem?>? = null,

    @field:SerializedName("total_results")
    val totalResults: Int? = null,
)
