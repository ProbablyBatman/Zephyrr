package greenberg.moviedbshell.models.container

import greenberg.moviedbshell.models.imagegallerymodels.ImageGalleryResponse
import greenberg.moviedbshell.models.moviedetailmodels.MovieDetailResponse
import greenberg.moviedbshell.models.movielistmodels.MovieListResponse
import greenberg.moviedbshell.models.peopledetailmodels.CombinedCreditsResponse
import greenberg.moviedbshell.models.peopledetailmodels.PersonDetailResponse
import greenberg.moviedbshell.models.sharedmodels.CreditsResponse
import greenberg.moviedbshell.models.tvdetailmodels.AggregateCreditsResponse
import greenberg.moviedbshell.models.tvdetailmodels.TvShowResponse
import greenberg.moviedbshell.models.tvlistmodels.TvListResponse

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
    val creditsResponse: CreditsResponse,
    val posterResponse: ImageGalleryResponse
)

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

/*
 * Part of a class of objects that exist as a sort of "wrapper"
 * In an attempt to make mapping multiple responses together easier, and cleaner, these objects are called containers
 * to wrap an arbitrary amount of responses that are logically bundled together to extract.
 *
 * Perhaps this is not a permanent solution but until further data level optimizations come along, but this is a functional
 * and clean enough stopgap.
 */
data class PersonDetailResponseContainer(
    val personDetailResponse: PersonDetailResponse,
    val creditsResponse: CombinedCreditsResponse
)

/*
 * Part of a class of objects that exist as a sort of "wrapper"
 * In an attempt to make mapping multiple responses together easier, and cleaner, these objects are called containers
 * to wrap an arbitrary amount of responses that are logically bundled together to extract.
 *
 * Perhaps this is not a permanent solution but until further data level optimizations come along, but this is a functional
 * and clean enough stopgap.
 */
data class TvDetailResponseContainer(
    val tvShowResponse: TvShowResponse,
    val creditsResponse: CreditsResponse,
    val aggregateCreditsResponse: AggregateCreditsResponse,
    val posterResponse: ImageGalleryResponse
)
