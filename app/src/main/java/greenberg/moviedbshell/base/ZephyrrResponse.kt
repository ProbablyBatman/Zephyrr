package greenberg.moviedbshell.base

sealed class ZephyrrResponse<out T> {
    data class Success<out T>(val value: T) : ZephyrrResponse<T>()
    data class Failure<out T>(val throwable: Throwable) : ZephyrrResponse<T>()
}
