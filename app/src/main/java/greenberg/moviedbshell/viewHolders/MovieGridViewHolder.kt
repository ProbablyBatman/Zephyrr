package greenberg.moviedbshell.viewHolders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import greenberg.moviedbshell.R

class MovieGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.movie_grid_item, parent, false))

    val poster = itemView.findViewById<ImageView>(R.id.movie_grid_poster)
}