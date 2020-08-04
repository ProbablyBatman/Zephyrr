package greenberg.moviedbshell.models.ui

data class PosterItem(
    val aspectRatio: Double,
    val filePath: String,
    val voteAverage: Double,
    val voteCount: Int,
    val width: Int,
    val height: Int
)