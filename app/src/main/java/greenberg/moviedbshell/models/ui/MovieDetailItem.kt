package greenberg.moviedbshell.models.ui

class MovieDetailItem(
    val movieId: Int,
    val movieTitle: String,
    val originalTitle: String,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val budget: Long,
    val runtime: Int,
    val status: String,
    val genres: List<String>,
    val posterImageUrl: String,
    val backdropImageUrl: String,
    val overview: String,
    val castMembers: List<CastMemberItem>
)