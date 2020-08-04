package greenberg.moviedbshell.models.ui

data class ProductionCountryItem(
    val name: String
) {
    companion object {
        fun generateDummy() = ProductionCountryItem("Unknown")
    }
}