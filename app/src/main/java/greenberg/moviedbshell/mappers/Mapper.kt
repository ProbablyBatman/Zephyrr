package greenberg.moviedbshell.mappers

interface Mapper<in S, out T> {
    fun mapToEntity(item: S): T
}