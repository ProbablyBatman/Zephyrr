package greenberg.moviedbshell.viewHolders

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
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