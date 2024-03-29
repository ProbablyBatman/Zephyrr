package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import greenberg.moviedbshell.models.searchmodels.SearchResponse
import greenberg.moviedbshell.models.ui.PreviewItem
import kotlinx.parcelize.Parcelize

data class SearchResultsState(
    val query: String = "",
    val pageNumber: Int = 1,
    val totalPages: Int = -1,
    val searchResultsResponse: Async<SearchResponse> = Uninitialized,
    val searchResults: List<PreviewItem> = emptyList()
) : MavericksState {
    constructor(args: SearchResultsArgs) : this(query = args.query)
}

@Parcelize
data class SearchResultsArgs(
    val query: String
) : Parcelable
