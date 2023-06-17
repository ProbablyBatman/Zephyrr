package greenberg.moviedbshell.models.ui

import greenberg.moviedbshell.models.MediaType

data class CreditsDetailItem(
    val title: String,
    val releaseDate: String,
    val role: String,
    val type: String,
    override val posterImageUrl: String,
    override val mediaType: MediaType,
    override val id: Int?,
) : PreviewItem(
    posterImageUrl = posterImageUrl,
    mediaType = mediaType,
    id = id,
)
