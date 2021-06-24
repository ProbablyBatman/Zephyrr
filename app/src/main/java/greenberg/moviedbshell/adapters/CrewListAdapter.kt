package greenberg.moviedbshell.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.ui.CrewMemberItem
import greenberg.moviedbshell.viewHolders.CrewListViewHolder

class CrewListAdapter(
    private var crewMemberList: List<CrewMemberItem> = listOf(),
    val onClickListener: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CrewListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.crew_list_card, parent, false))
    }

    override fun getItemCount() = crewMemberList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CrewListViewHolder) {
            resetView(holder)
            val currentItem = crewMemberList[position]
            holder.crewJob.text = currentItem.job
            holder.crewName.text = currentItem.name
            // Set poster image
            val validUrl = holder.crewImage.context.getString(R.string.poster_url_substitution, currentItem.posterUrl)
            if (validUrl.isNotEmpty()) {
                Glide.with(holder.crewImage)
                    .load(validUrl)
                    .apply(
                        RequestOptions()
                            .placeholder(ColorDrawable(Color.LTGRAY))
                            .fallback(ColorDrawable(Color.LTGRAY))
                            .centerCrop()
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.crewImage)
            }
            holder.cardView.setOnClickListener { onClickListener.invoke(currentItem.id ?: -1) }
        }
    }

    fun setMembersList(items: List<CrewMemberItem>) {
        crewMemberList = items
        notifyDataSetChanged()
    }

    private fun resetView(holder: CrewListViewHolder) {
        holder.crewJob.text = ""
        holder.crewName.text = ""
        Glide.with(holder.crewImage).clear(holder.crewImage)
    }
}