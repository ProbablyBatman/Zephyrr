package greenberg.moviedbshell.models.ui

import greenberg.moviedbshell.presenters.SearchPresenter

class PersonItem(
    val name: String,
    posterImageUrl: String,
    val knownForItems: List<PreviewItem>,
    id: Int?
) : PreviewItem(
        posterImageUrl,
        id,
        SearchPresenter.MEDIA_TYPE_PERSON)