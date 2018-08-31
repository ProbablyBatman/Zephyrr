package greenberg.moviedbshell.models.ui

class TvDetailItem(
        val title: String,
        val originalTitle: String,
        val firstAirDate: String,
        val lastAirDate: String?,
        val nextAirDate: String?,
        val status: String,
        val numberOfEpisodes: Int,
        val numberOfSeasons: Int,
        val voteAverage: Double,
        val voteCount: Int,
        val runtime: List<Int>,
        val genres: List<String>,
        val posterImageUrl: String,
        val backgroundImageUrl: String,
        val overview: String,
        val castMembers: List<CastMemberItem>
)