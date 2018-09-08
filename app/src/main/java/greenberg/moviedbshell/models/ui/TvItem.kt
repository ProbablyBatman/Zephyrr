package greenberg.moviedbshell.models.ui

import greenberg.moviedbshell.presenters.SearchPresenter

class TvItem(
    val name: String,
    val overview: String,
    val firstAirDate: String,
    posterImageUrl: String,
    id: Int?
) : PreviewItem(
        posterImageUrl,
        id,
        SearchPresenter.MEDIA_TYPE_TV)