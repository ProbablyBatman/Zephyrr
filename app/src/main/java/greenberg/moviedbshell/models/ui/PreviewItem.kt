package greenberg.moviedbshell.models.ui

import greenberg.moviedbshell.models.MediaType

open class PreviewItem(
    open val posterImageUrl: String = "",
    open val id: Int? = null,
    open val mediaType: MediaType
)
