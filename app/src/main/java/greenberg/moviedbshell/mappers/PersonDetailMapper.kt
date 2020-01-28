package greenberg.moviedbshell.mappers

import greenberg.moviedbshell.models.peopledetailmodels.PersonDetailResponseContainer
import greenberg.moviedbshell.models.ui.PersonDetailItem
import javax.inject.Inject

class PersonDetailMapper
@Inject constructor(private val creditMapper: PersonDetailCreditMapper) : Mapper<PersonDetailResponseContainer, PersonDetailItem> {
    override fun mapToEntity(item: PersonDetailResponseContainer?): PersonDetailItem {
        val personDetailResponse = item?.personDetailResponse
        val creditsResponse = item?.creditsResponse
        return PersonDetailItem(
                name = personDetailResponse?.name.orEmpty(),
                birthday = personDetailResponse?.birthday.orEmpty(),
                deathday = personDetailResponse?.deathday.orEmpty(),
                placeOfBirth = personDetailResponse?.placeOfBirth.orEmpty(),
                biography = personDetailResponse?.biography.orEmpty(),
                posterImageUrl = personDetailResponse?.profilePath.orEmpty(),
                combinedCredits = creditMapper.mapToEntity(creditsResponse)
        )
    }
}