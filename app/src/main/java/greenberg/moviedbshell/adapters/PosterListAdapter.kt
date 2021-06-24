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
import greenberg.moviedbshell.models.ui.PosterItem
import greenberg.moviedbshell.viewHolders.PosterListViewHolder

class PosterListAdapter(
    var posterItems: List<PosterItem> = emptyList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PosterListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.poster_list_item, parent, false))
    }

    override fun getItemCount() = posterItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PosterListViewHolder) {
            resetView(holder)
            val currentItem = posterItems[position]
            if (currentItem.filePath.isNotEmpty()) {
                Glide.with(holder.view)
                    .load(holder.view.context.resources.getString(R.string.poster_url_substitution, currentItem.filePath))
                    .apply(
                        RequestOptions()
                            .placeholder(ColorDrawable(Color.LTGRAY))
                            .fallback(ColorDrawable(Color.LTGRAY))
                            .centerCrop()
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.poster)
            }
        }
    }

    private fun resetView(holder: PosterListViewHolder) {
        Glide.with(holder.poster).clear(holder.poster)
    }
}
