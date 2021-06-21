package greenberg.moviedbshell.models.ui

data class CreditsDetailItem(
    val title: String,
    val releaseDate: String,
    val role: String,
    val type: String,
    override val posterImageUrl: String,
    override val mediaType: String,
    override val id: Int?
) : PreviewItem(
        posterImageUrl = posterImageUrl,
        mediaType = mediaType,
        id = id
)