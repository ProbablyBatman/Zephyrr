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
            when (castItem?.mediaType) {
                MediaType.MOVIE -> {
                    CreditsDetailItem(
                            title = castItem.title.orEmpty(),
                            releaseDate = castItem.releaseDate.orEmpty(),
                            role = castItem.character.orEmpty(),
                            type = Person.CAST,
                            posterImageUrl = castItem.posterPath.orEmpty(),
                            mediaType = castItem.mediaType,
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
                            mediaType = castItem.mediaType,
                            id = castItem.id
                    )
                }
                else -> {
                    null
                }
            }
        } ?: emptyList()
        val crewList = item?.personCrewResponse?.mapNotNull { crewItem ->
            when (crewItem?.mediaType) {
                MediaType.MOVIE -> {
                    CreditsDetailItem(
                            title = crewItem.title.orEmpty(),
                            releaseDate = crewItem.releaseDate.orEmpty(),
                            role = crewItem.job.orEmpty(),
                            type = Person.CREW,
                            posterImageUrl = crewItem.posterPath.orEmpty(),
                            mediaType = crewItem.mediaType,
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
                            mediaType = crewItem.mediaType,
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