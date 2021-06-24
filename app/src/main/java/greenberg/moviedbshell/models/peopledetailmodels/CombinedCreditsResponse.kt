package greenberg.moviedbshell.models.peopledetailmodels

import com.google.gson.annotations.SerializedName

data class CombinedCreditsResponse(

    @field:SerializedName("cast")
    val personCastResponse: List<PersonCastResponseItem?>? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("crew")
    val personCrewResponse: List<PersonCrewResponseItem?>? = null
)
