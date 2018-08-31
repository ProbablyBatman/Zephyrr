package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.peopledetailmodels.CombinedCreditsResponse
import greenberg.moviedbshell.models.ui.PersonDetailCreditItem
import greenberg.moviedbshell.presenters.SearchPresenter
import javax.inject.Inject

class PersonDetailCreditMapper
@Inject constructor() : Mapper<CombinedCreditsResponse, List<PersonDetailCreditItem>> {
    override fun mapToEntity(item: CombinedCreditsResponse): List<PersonDetailCreditItem> {
        return item.personCastResponse?.mapNotNull { castItem ->
            when (castItem?.mediaType) {
                SearchPresenter.MEDIA_TYPE_MOVIE -> {
                    PersonDetailCreditItem(
                            title = castItem.title.orEmpty(),
                            releaseDate = castItem.releaseDate.orEmpty(),
                            role = castItem.character.orEmpty(),
                            posterImageUrl = castItem.posterPath.orEmpty(),
                            mediaType = castItem.mediaType,
                            id = castItem.id
                    )
                }
                SearchPresenter.MEDIA_TYPE_TV -> {
                    PersonDetailCreditItem(
                            title = castItem.name.orEmpty(),
                            releaseDate = castItem.firstAirDate.orEmpty(),
                            role = castItem.character.orEmpty(),
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
    }
}