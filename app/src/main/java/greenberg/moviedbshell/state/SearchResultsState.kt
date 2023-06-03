package greenberg.moviedbshell.state

import android.os.Parcelable
import greenberg.moviedbshell.models.searchmodels.SearchResponse
import greenberg.moviedbshell.models.ui.PreviewItem
import kotlinx.parcelize.Parcelize

data class SearchResultsState(
    val query: String = "",
    val pageNumber: Int = 1,
    val totalPages: Int = -1,
    val searchResults: List<PreviewItem> = emptyList(),
    val error: Throwable? = null,
    val isLoading: Boolean = true
)

@Parcelize
data class SearchResultsArgs(
    val query: String = "",
    val usingMultiSearch: Boolean = false
) : Parcelable
