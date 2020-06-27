package greenberg.moviedbshell.models.container

import greenberg.moviedbshell.models.popularmoviesmodels.PopularMovieResponse
import greenberg.moviedbshell.models.recentlyreleasedmodels.RecentlyReleasedResponse
import greenberg.moviedbshell.models.soontmmodels.SoonTMResponse

/*
 * Part of a class of objects that exist as a sort of "wrapper"
 * In an attempt to make mapping multiple responses together easier, and cleaner, these objects are called containers
 * to wrap an arbitrary amount of responses that are logically bundled together to extract.
 *
 * Perhaps this is not a permanent solution but until further data level optimizations come along, but this is a functional
 * and clean enough stopgap.
 */
data class LandingContainer(
    val recentlyReleasedResponse: RecentlyReleasedResponse,
    val popularMovieResponse: PopularMovieResponse,
    val soonTMResponse: SoonTMResponse
)