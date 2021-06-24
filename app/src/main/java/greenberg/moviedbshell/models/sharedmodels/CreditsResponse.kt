package greenberg.moviedbshell.models.sharedmodels

import com.google.gson.annotations.SerializedName

data class CreditsResponse(

    @field:SerializedName("cast")
    val cast: List<CastResponseItem?>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("crew")
    val crew: List<CrewResponseItem?>? = null
)
