package greenberg.moviedbshell.viewHolders

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import greenberg.moviedbshell.models.SearchModels.SearchResultsItem
import greenberg.moviedbshell.mosbyImpl.SearchPresenter
import greenberg.moviedbshell.R

class SearchResultsAdapter(var searchResults: MutableList<SearchResultsItem?> = mutableListOf(),
                           private val presenter: SearchPresenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SearchResultsViewHolder) {
            val currentItem = searchResults[position]
            when (currentItem?.mediaType) {
                MEDIA_TYPE_MOVIE -> {
                    holder.searchItemTitle.text = currentItem.title
                    holder.searchItemSubInfo.text = currentItem.releaseDate?.let { presenter.processReleaseDate(it) }
                    holder.searchItemOverview.text = currentItem.overview
                    presenter.fetchPosterArt(holder.searchItemPosterImage, currentItem)
                }
                MEDIA_TYPE_TV -> {
                    holder.searchItemTitle.text = currentItem.name
                    holder.searchItemSubInfo.text = currentItem.firstAirDate?.let { presenter.processReleaseDate(it) }
                    holder.searchItemOverview.text = currentItem.overview
                    presenter.fetchPosterArt(holder.searchItemPosterImage, currentItem)
                }
                MEDIA_TYPE_PERSON -> {
                    //One of the problems with people is their limited fields.  Currently they only have:
                    //popularity, media type, id, profile path (for picture), name, and known for (list of movies).
                    holder.searchItemTitle.text = currentItem.name
                    //todo: find better way to do these
                    holder.searchItemSubInfo.text = ""
                    holder.searchItemOverview.text = ""
                    presenter.fetchPosterArt(holder.searchItemPosterImage, currentItem)
                }
                else -> {
                    //TODO: handle unknown type?
                    //Can any information be assumed?  For now place unknown
                }
            }
        }
    }

    override fun getItemCount() = searchResults.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchResultsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_card_layout, parent, false))
    }

    class SearchResultsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var searchItemPosterImage: ImageView = view.findViewById(R.id.search_item_poster_image)
        var searchItemTitle: TextView = view.findViewById(R.id.search_item_title)
        //Used for release date, or alternatively info line of "Actor"
        var searchItemSubInfo: TextView = view.findViewById(R.id.search_item_sub_info_line)
        //Used for movies, blank for actors.
        //TODO: potentially add well known movies provided by api to this
        var searchItemOverview: TextView = view.findViewById(R.id.search_item_overview)
    }

    companion object {
        const val MEDIA_TYPE_PERSON = "person"
        const val MEDIA_TYPE_MOVIE = "movie"
        const val MEDIA_TYPE_TV = "tv"
    }
}