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
import greenberg.moviedbshell.models.ui.PreviewItem
import greenberg.moviedbshell.viewHolders.LandingViewHolder

class LandingAdapter(
    private var items: List<PreviewItem> = emptyList(),
    val posterClickListener: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LandingViewHolder) {
            val currentItem = items[position]
            if (currentItem.posterImageUrl.isNotEmpty()) {
                Glide.with(holder.poster)
                    .load(holder.poster.context.getString(R.string.poster_url_substitution, currentItem.posterImageUrl))
                    .apply(
                        RequestOptions()
                            .placeholder(ColorDrawable(Color.LTGRAY))
                            .fallback(ColorDrawable(Color.LTGRAY))
                            .centerCrop()
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.poster)
            }
            holder.poster.setOnClickListener { posterClickListener(currentItem.id ?: -1) }
        }
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LandingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.landing_poster_layout, parent, false))
    }

    fun setItems(newItems: List<PreviewItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
