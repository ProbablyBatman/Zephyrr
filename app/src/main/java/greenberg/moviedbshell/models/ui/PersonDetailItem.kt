package greenberg.moviedbshell.models.ui

class PersonDetailItem(
        val name: String,
        val birthday: String,
        val placeOfBirth: String,
        val biography: String,
        val posterImageUrl: String,
        val combinedCredits: List<PersonDetailCreditItem>
)