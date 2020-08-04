package greenberg.moviedbshell.models.ui

data class ProductionCompanyItem(
    val logoPath: String,
    val name: String,
    val originCountry: String
) {
    companion object {
        fun generateDummy() = ProductionCompanyItem("", "Unknown", "")
    }
}