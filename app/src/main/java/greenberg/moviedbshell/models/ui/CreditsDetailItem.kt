package greenberg.moviedbshell.models.ui

class CreditsDetailItem(
    val title: String,
    val releaseDate: String,
    val role: String,
    val type: String,
    posterImageUrl: String,
    mediaType: String,
    id: Int?
) : PreviewItem(
        posterImageUrl = posterImageUrl,
        mediaType = mediaType,
        id = id
)