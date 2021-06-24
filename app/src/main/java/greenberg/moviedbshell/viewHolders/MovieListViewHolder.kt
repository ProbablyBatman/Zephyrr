package greenberg.moviedbshell.viewHolders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import greenberg.moviedbshell.R

class MovieListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.movie_list_card, parent, false))

    val poster = itemView.findViewById<ImageView>(R.id.movie_list_card_poster)
    val title = itemView.findViewById<TextView>(R.id.movie_list_card_title)
    val overview = itemView.findViewById<TextView>(R.id.movie_list_card_overview)
}
