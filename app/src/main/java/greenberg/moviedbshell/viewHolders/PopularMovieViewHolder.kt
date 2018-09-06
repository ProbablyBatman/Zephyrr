package greenberg.moviedbshell.viewHolders

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import greenberg.moviedbshell.R

class PopularMovieViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var cardItemPosterImage: ImageView = view.findViewById(R.id.cardItemPosterImage)
    var cardItemTitle: TextView = view.findViewById(R.id.cardItemTitle)
    var cardItemReleaseDate: TextView = view.findViewById(R.id.cardItemReleaseDate)
    var cardItemOverview: TextView = view.findViewById(R.id.cardItemOverview)
    var cardItem: CardView = view.findViewById(R.id.popularMovieCard)
}