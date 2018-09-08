package greenberg.moviedbshell.models.ui

class PersonDetailCreditItem(
    val title: String,
    val releaseDate: String,
    val role: String,
    posterImageUrl: String,
    mediaType: String,
    id: Int?
) : PreviewItem(
        posterImageUrl = posterImageUrl,
        mediaType = mediaType,
        id = id
)