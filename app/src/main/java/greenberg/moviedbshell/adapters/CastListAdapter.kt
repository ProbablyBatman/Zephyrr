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
import greenberg.moviedbshell.models.ui.CastMemberItem
import greenberg.moviedbshell.viewHolders.CastListViewHolder

class CastListAdapter(
    var castMemberList: List<CastMemberItem> = listOf(),
    val onClickListener: (Int) -> Unit,
    val isBubble: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // TODO: this almost feels like code smell, but I don't see why it's bad.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CastListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                if (isBubble) R.layout.cast_list_bubble
                else R.layout.cast_list_card,
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
            holder.actorRole.text = currentItem.role
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

    private fun resetView(holder: CastListViewHolder) {
        holder.actorRole.text = ""
        holder.actorName.text = ""
        Glide.with(holder.actorImage).clear(holder.actorImage)
    }
}