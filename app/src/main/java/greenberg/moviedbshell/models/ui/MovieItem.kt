package greenberg.moviedbshell.models.ui

import greenberg.moviedbshell.models.MediaType

// TODO: add support for non-English movie titles to show both translated and original
// TODO: add support for different views and showing background images
data class MovieItem(
    val movieTitle: String,
    val overview: String,
    val releaseDate: String,
    override val posterImageUrl: String,
    override val id: Int?,
) : PreviewItem(
    posterImageUrl,
    id,
    MediaType.MOVIE,
)
