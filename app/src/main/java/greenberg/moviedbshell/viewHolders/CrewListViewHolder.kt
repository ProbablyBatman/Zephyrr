package greenberg.moviedbshell.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import greenberg.moviedbshell.R

class CrewListViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var cardView: MaterialCardView = view.findViewById(R.id.crew_card_view)
    var crewImage: ImageView = view.findViewById(R.id.crew_list_item_image)
    var crewJob: TextView = view.findViewById(R.id.crew_list_item_job)
    var crewName: TextView = view.findViewById(R.id.crew_list_item_name)
}
