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
import greenberg.moviedbshell.models.ui.AggregateCastMemberItem
import greenberg.moviedbshell.models.ui.CastMemberItem
import greenberg.moviedbshell.viewHolders.CastListViewHolder

class CastListAdapter(
    val onClickListener: (Int) -> Unit,
    val isBubble: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var castMemberList: List<CastMemberItem> = listOf()

    // TODO: this almost feels like code smell, but I don't see why it's bad.
    // Update for this commit, I made this worse and now see why it could be bad.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = when {
            isBubble -> R.layout.cast_list_bubble
            castMemberList.all { it is AggregateCastMemberItem } -> R.layout.aggregate_cast_list_card
            else -> R.layout.cast_list_card
        }
        return CastListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = castMemberList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CastListViewHolder) {
            resetView(holder)
            val currentItem = castMemberList[position]
            val roleText = if (currentItem is AggregateCastMemberItem) {
                currentItem.roles.joinToString("\n", transform = { "${it.character} (${it.episodeCount})" })
            } else {
                currentItem.role
            }
            holder.actorRole.text = roleText
            holder.actorName.text = currentItem.name
            // Set poster image
            val validUrl = holder.actorImage.context.getString(R.string.poster_url_substitution, currentItem.posterUrl)
            if (validUrl.isNotEmpty()) {
                Glide.with(holder.actorImage)
                    .load(validUrl)
                    .apply(
                        RequestOptions()
                            .placeholder(ColorDrawable(Color.LTGRAY))
                            .fallback(ColorDrawable(Color.LTGRAY))
                            .centerCrop()
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.actorImage)
            }
            holder.itemView.setOnClickListener { onClickListener.invoke(currentItem.id ?: -1) }
        }
    }

    fun setCastMembers(members: List<CastMemberItem>) {
        castMemberList = members
        notifyDataSetChanged()
    }

    private fun resetView(holder: CastListViewHolder) {
        holder.actorRole.text = ""
        holder.actorName.text = ""
        Glide.with(holder.actorImage).clear(holder.actorImage)
    }
}
