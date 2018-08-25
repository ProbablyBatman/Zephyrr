package greenberg.moviedbshell.models.tvdetailmodels

import com.google.gson.annotations.SerializedName
import greenberg.moviedbshell.models.sharedmodels.CastResponseItem

data class TvDetailCreditsResponse(

	@field:SerializedName("cast")
	val cast: List<CastResponseItem?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("crew")
	val crew: List<CrewItem?>? = null
)