package greenberg.moviedbshell.viewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import greenberg.moviedbshell.R

class ProductionDetailCrewListViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val crewTitle: TextView = view.findViewById(R.id.production_detail_crew_title)
    val crewName: TextView = view.findViewById(R.id.production_detail_crew_name)
}
