package greenberg.moviedbshell.models.ui

data class LandingItem(
    val recentlyReleasedItems: List<MovieItem>,
    val popularMovieItems: List<MovieItem>,
    val soonTMItems: List<MovieItem>
)