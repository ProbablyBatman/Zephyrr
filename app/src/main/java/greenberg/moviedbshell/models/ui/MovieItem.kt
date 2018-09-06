package greenberg.moviedbshell.models.ui

import greenberg.moviedbshell.presenters.SearchPresenter

// TODO: add support for non-English movie titles to show both translated and original
// TODO: add support for different views and showing background images
class MovieItem(
    val movieTitle: String,
    val overview: String,
    val releaseDate: String,
    posterImageUrl: String,
    id: Int?
) : PreviewItem(
        posterImageUrl,
        id,
        SearchPresenter.MEDIA_TYPE_MOVIE)