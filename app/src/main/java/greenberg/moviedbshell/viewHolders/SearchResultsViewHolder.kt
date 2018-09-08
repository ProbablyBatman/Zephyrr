package greenberg.moviedbshell.viewHolders

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import greenberg.moviedbshell.R

class SearchResultsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var searchItemPosterImage: ImageView = view.findViewById(R.id.search_item_poster_image)
    var searchItemTitle: TextView = view.findViewById(R.id.search_item_title)
    // Used for release date, or alternatively info line of "Actor"
    var searchItemSubInfo: TextView = view.findViewById(R.id.search_item_sub_info_line)
    // Used for movies, known for list for actors.
    var searchItemOverview: TextView = view.findViewById(R.id.search_item_overview)
    var cardItem: CardView = view.findViewById(R.id.search_result_card)
}