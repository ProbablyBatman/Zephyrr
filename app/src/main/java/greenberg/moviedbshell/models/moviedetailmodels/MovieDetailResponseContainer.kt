package greenberg.moviedbshell.models.moviedetailmodels

import greenberg.moviedbshell.models.sharedmodels.CreditsResponse

/*
 * Part of a class of objects that exist as a sort of "wrapper"
 * In an attempt to make mapping multiple responses together easier, and cleaner, these objects are called containers
 * to wrap an arbitrary amount of responses that are logically bundled together to extract.
 *
 * Perhaps this is not a permanent solution but until further data level optimizations come along, but this is a functional
 * and clean enough stopgap.
 */
data class MovieDetailResponseContainer(
    val movieDetailResponse: MovieDetailResponse,
    val creditsResponse: CreditsResponse
)