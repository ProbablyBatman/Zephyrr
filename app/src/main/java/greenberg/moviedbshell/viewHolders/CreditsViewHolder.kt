package greenberg.moviedbshell.viewHolders

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import greenberg.moviedbshell.R

class CreditsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val cardView: CardView = view.findViewById(R.id.person_detail_card)
    val actorImage: ImageView = view.findViewById(R.id.credit_list_item_image)
    val itemTitle: TextView = view.findViewById(R.id.credit_list_item_title)
    val releaseDate: TextView = view.findViewById(R.id.credit_list_item_release_date)
    val actorRole: TextView = view.findViewById(R.id.credit_list_item_role)
}