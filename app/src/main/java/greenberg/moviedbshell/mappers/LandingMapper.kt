package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.container.LandingContainer
import greenberg.moviedbshell.models.ui.LandingItem
import javax.inject.Inject

class LandingMapper
@Inject constructor(
    private val movieListMapper: MovieListMapper
) : Mapper<LandingContainer, LandingItem> {
    override fun mapToEntity(item: LandingContainer?): LandingItem =
            LandingItem(
                movieListMapper.mapToEntity(item?.recentlyReleasedResponse),
                movieListMapper.mapToEntity(item?.popularMovieResponse),
                movieListMapper.mapToEntity(item?.soonTMResponse)
            )
}