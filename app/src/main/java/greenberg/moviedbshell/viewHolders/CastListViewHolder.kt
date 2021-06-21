package greenberg.moviedbshell.viewHolders

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import greenberg.moviedbshell.R

class CastListViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val actorImage: ImageView = view.findViewById(R.id.cast_list_item_image)
    val actorRole: TextView = view.findViewById(R.id.cast_list_item_role)
    val actorName: TextView = view.findViewById(R.id.cast_list_item_actor)
}