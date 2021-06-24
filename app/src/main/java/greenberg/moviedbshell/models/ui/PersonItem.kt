package greenberg.moviedbshell.models.ui

import greenberg.moviedbshell.models.MediaType

data class PersonItem(
    val name: String,
    override val posterImageUrl: String,
    val knownForItems: List<PreviewItem>,
    override val id: Int?
) : PreviewItem(
    posterImageUrl,
    id,
    MediaType.PERSON
)
