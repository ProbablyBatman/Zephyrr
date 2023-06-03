package greenberg.moviedbshell.state

import greenberg.moviedbshell.models.ui.PersonItem

data class MultiSearchState(
    val currentQuery: String = "",
    val queries: List<PersonItem> = emptyList(),
)
