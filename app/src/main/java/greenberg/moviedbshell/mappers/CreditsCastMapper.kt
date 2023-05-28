package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.models.Person
import greenberg.moviedbshell.models.peopledetailmodels.CombinedCreditsResponse
import greenberg.moviedbshell.models.ui.CreditsDetailItem
import javax.inject.Inject

class CreditsCastMapper
@Inject constructor() : Mapper<CombinedCreditsResponse, List<CreditsDetailItem>> {
    override fun mapToEntity(item: CombinedCreditsResponse?): List<CreditsDetailItem> {
        val castList = item?.personCastResponse?.mapNotNull { castItem ->
            when (val castMediaType = castItem?.mediaType?.let { MediaType.valueOf(it) }) {
                MediaType.MOVIE -> {
                    CreditsDetailItem(
                        title = castItem.title.orEmpty(),
                        releaseDate = castItem.releaseDate.orEmpty(),
                        role = castItem.character.orEmpty(),
                        type = Person.CAST,
                        posterImageUrl = castItem.posterPath.orEmpty(),
                        mediaType = castMediaType,
                        id = castItem.id
                    )
                }
                MediaType.TV -> {
                    CreditsDetailItem(
                        title = castItem.name.orEmpty(),
                        releaseDate = castItem.firstAirDate.orEmpty(),
                        role = castItem.character.orEmpty(),
                        type = Person.CAST,
                        posterImageUrl = castItem.posterPath.orEmpty(),
                        mediaType = castMediaType,
                        id = castItem.id
                    )
                }
                else -> {
                    null
                }
            }
        } ?: emptyList()
        val crewList = item?.personCrewResponse?.mapNotNull { crewItem ->
            when (val crewMediaType = crewItem?.mediaType?.let { MediaType.valueOf(it) }) {
                MediaType.MOVIE -> {
                    CreditsDetailItem(
                        title = crewItem.title.orEmpty(),
                        releaseDate = crewItem.releaseDate.orEmpty(),
                        role = crewItem.job.orEmpty(),
                        type = Person.CREW,
                        posterImageUrl = crewItem.posterPath.orEmpty(),
                        mediaType = crewMediaType,
                        id = crewItem.id
                    )
                }
                MediaType.TV -> {
                    CreditsDetailItem(
                        title = crewItem.name.orEmpty(),
                        releaseDate = crewItem.releaseDate.orEmpty(),
                        role = crewItem.job.orEmpty(),
                        type = Person.CREW,
                        posterImageUrl = crewItem.posterPath.orEmpty(),
                        mediaType = crewMediaType,
                        id = crewItem.id
                    )
                }
                else -> {
                    null
                }
            }
        } ?: emptyList()
        return castList + crewList
    }
}
