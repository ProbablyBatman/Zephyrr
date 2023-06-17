package greenberg.moviedbshell.models.tvdetailmodels

import com.google.gson.annotations.SerializedName

data class AggregateCreditsResponse(
    @field:SerializedName("cast")
    val cast: List<AggregateCastResponseItem?>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("crew")
    val crew: List<AggregateCrewResponseItem?>? = null,
)
