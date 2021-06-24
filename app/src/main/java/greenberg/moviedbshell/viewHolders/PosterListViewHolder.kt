package greenberg.moviedbshell.viewHolders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import greenberg.moviedbshell.R

class PosterListViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val poster = view.findViewById<ImageView>(R.id.poster_list_image)
}
