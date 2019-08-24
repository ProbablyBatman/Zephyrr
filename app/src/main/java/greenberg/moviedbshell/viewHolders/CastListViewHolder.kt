package greenberg.moviedbshell.viewHolders

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import greenberg.moviedbshell.R

class CastListViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var cardView: CardView = view.findViewById(R.id.cast_card_view)
    var actorImage: ImageView = view.findViewById(R.id.cast_list_item_image)
    var actorRole: TextView = view.findViewById(R.id.cast_list_item_role)
    var actorName: TextView = view.findViewById(R.id.cast_list_item_actor)
}