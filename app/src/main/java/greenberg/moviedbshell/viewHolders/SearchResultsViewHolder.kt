package greenberg.moviedbshell.viewHolders

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.models.ui.PersonItem
import greenberg.moviedbshell.models.ui.PreviewItem
import greenberg.moviedbshell.models.ui.TvItem
import greenberg.moviedbshell.presenters.SearchPresenter

class SearchResultsAdapter(var searchResults: MutableList<PreviewItem> = mutableListOf(),
                           private val presenter: SearchPresenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SearchResultsViewHolder) {
            //Reset text, images, and listeners
            holder.searchItemTitle.text = ""
            holder.searchItemSubInfo.text = ""
            holder.searchItemOverview.text = ""
            holder.cardItem.setOnClickListener(null)
            val currentItem = searchResults[position]
            when (currentItem) {
                is MovieItem -> {
                    holder.searchItemTitle.text = currentItem.movieTitle
                    holder.searchItemSubInfo.text = presenter.processReleaseDate(currentItem.releaseDate)
                    holder.searchItemOverview.text = currentItem.overview
                    presenter.fetchPosterArt(holder.searchItemPosterImage, currentItem)
                    holder.cardItem.setOnClickListener { presenter.onCardSelected(currentItem.id ?: -1, currentItem.mediaType) }
                }
                is TvItem -> {
                    holder.searchItemTitle.text = currentItem.name
                    holder.searchItemSubInfo.text = presenter.processReleaseDate(currentItem.firstAirDate)
                    holder.searchItemOverview.text = currentItem.overview
                    presenter.fetchPosterArt(holder.searchItemPosterImage, currentItem)
                    holder.cardItem.setOnClickListener { presenter.onCardSelected(currentItem.id ?: -1, currentItem.mediaType) }
                }
                is PersonItem -> {
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
        var cardItem: CardView = view.findViewById(R.id.search_result_card)
    }
}