package greenberg.moviedbshell.models.container

import greenberg.moviedbshell.models.movielistmodels.MovieListResponse
import greenberg.moviedbshell.models.tvlistmodels.TvListResponse

/*
 * Part of a class of objects that exist as a sort of "wrapper"
 * In an attempt to make mapping multiple responses together easier, and cleaner, these objects are called containers
 * to wrap an arbitrary amount of responses that are logically bundled together to extract.
 *
 * Perhaps this is not a permanent solution but until further data level optimizations come along, but this is a functional
 * and clean enough stopgap.
 */
data class LandingContainer(
    val recentlyReleasedResponse: MovieListResponse,
    val popularMovieResponse: MovieListResponse,
    val soonTMResponse: MovieListResponse,
    val popularTvResponse: TvListResponse,
    val topRatedTvResponse: TvListResponse
)