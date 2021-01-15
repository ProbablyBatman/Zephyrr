package greenberg.moviedbshell.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.ui.CrewMemberItem
import greenberg.moviedbshell.viewHolders.ProductionDetailCrewListViewHolder

class ProductionDetailAdapter(
    var crewMemberList: List<CrewMemberItem> = listOf(),
    val onClickListener: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductionDetailCrewListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.production_detail_crew_item, parent, false))
    }

    override fun getItemCount() = crewMemberList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductionDetailCrewListViewHolder) {
            val currentItem = crewMemberList[position]
            holder.crewTitle.text = currentItem.job
            holder.crewName.text = currentItem.name
            holder.itemView.setOnClickListener { onClickListener(currentItem.id ?: -1) }
        }
    }
}
