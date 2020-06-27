package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.container.LandingContainer
import greenberg.moviedbshell.models.ui.LandingItem
import javax.inject.Inject

class LandingMapper
@Inject constructor(
    private val popularMovieMapper: PopularMovieMapper,
    private val recentlyReleasedMapper: RecentlyReleasedMapper,
    private val soonTMMapper: SoonTMMapper
) : Mapper<LandingContainer, LandingItem> {
    override fun mapToEntity(item: LandingContainer?): LandingItem =
            LandingItem(
                    recentlyReleasedMapper.mapToEntity(item?.recentlyReleasedResponse),
                    popularMovieMapper.mapToEntity(item?.popularMovieResponse),
                    soonTMMapper.mapToEntity(item?.soonTMResponse)
            )
}