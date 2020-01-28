package greenberg.moviedbshell.models.ui

import greenberg.moviedbshell.models.MediaType

data class TvItem(
    val name: String,
    val overview: String,
    val firstAirDate: String,
    override val posterImageUrl: String,
    override val id: Int?
) : PreviewItem(
        posterImageUrl,
        id,
        MediaType.TV)