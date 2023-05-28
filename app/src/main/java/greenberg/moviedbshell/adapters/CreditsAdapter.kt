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
import greenberg.moviedbshell.extensions.processAsReleaseDate
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.models.ui.CreditsDetailItem
import greenberg.moviedbshell.viewHolders.CreditsViewHolder

class CreditsAdapter(
    var creditsList: List<CreditsDetailItem> = listOf(),
    val onClickListener: (Int, MediaType) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CreditsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.credit_list_card, parent, false))
    }

    override fun getItemCount() = creditsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CreditsViewHolder) {
            resetView(holder)
            val currentItem = creditsList[position]
            if (currentItem.posterImageUrl.isNotEmpty()) {
                Glide.with(holder.actorImage)
                    .load(holder.actorImage.context.getString(R.string.poster_url_substitution, currentItem.posterImageUrl))
                    .apply(
                        RequestOptions()
                            .placeholder(ColorDrawable(Color.LTGRAY))
                            .fallback(ColorDrawable(Color.LTGRAY))
                            .centerCrop()
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.actorImage)
            }
            holder.itemTitle.text = currentItem.title
            holder.releaseDate.text = currentItem.releaseDate.processAsReleaseDate()
            holder.actorRole.text = currentItem.role
            holder.cardView.setOnClickListener {
                onClickListener(
                    currentItem.id ?: -1,
                    currentItem.mediaType
                )
            }
        }
    }

    private fun resetView(holder: CreditsViewHolder) {
        Glide.with(holder.actorImage).clear(holder.actorImage)
        holder.itemTitle.text = ""
        holder.releaseDate.text = ""
        holder.actorRole.text = ""
        holder.cardView.setOnClickListener(null)
    }
}
