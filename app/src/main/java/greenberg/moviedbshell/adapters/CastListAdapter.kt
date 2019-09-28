package greenberg.moviedbshell.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.ui.CastMemberItem
import greenberg.moviedbshell.viewHolders.CastListViewHolder

class CastListAdapter(var castMemberList: MutableList<CastMemberItem> = mutableListOf()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var onClickListener: (Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CastListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cast_list_card, parent, false))
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
            holder.cardView.setOnClickListener { onClickListener.invoke(currentItem.id ?: -1) }
        }
    }

    private fun resetView(holder: CastListViewHolder) {
        holder.actorRole.text = ""
        holder.actorName.text = ""
        Glide.with(holder.actorImage).clear(holder.actorImage)
    }
}