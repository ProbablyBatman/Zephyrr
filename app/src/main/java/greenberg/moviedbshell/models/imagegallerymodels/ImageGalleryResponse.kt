package greenberg.moviedbshell.models.imagegallerymodels

import com.google.gson.annotations.SerializedName

data class ImageGalleryResponse(

    @field:SerializedName("backdrops")
    val backdrops: List<BackdropsItem?>? = null,

    @field:SerializedName("posters")
    val posters: List<PostersItem?>? = null,

    @field:SerializedName("id")
    val id: Int? = null
)
