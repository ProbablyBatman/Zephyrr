package greenberg.moviedbshell.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.ui.PosterItem
import greenberg.moviedbshell.viewHolders.ImageGalleryViewHolder

class ImageGalleryAdapter(
    var posters: List<PosterItem> = listOf(),
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageGalleryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_gallery_single_layout, parent, false))
    }

    override fun getItemCount() = posters.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = posters[position]
        if (holder is ImageGalleryViewHolder) {
            resetView(holder)
            Glide.with(holder.imageView)
                .load(holder.imageView.context.getString(R.string.poster_url_substitution, currentItem.filePath))
                .apply(RequestOptions().fitCenter())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView)
            // Show the current position / number of items
            holder.counter.text = holder.counter.context.getString(R.string.image_gallery_counter, position + 1, itemCount)
        }
    }

    // why not just page the bottom sheet ?
    private fun resetView(holder: ImageGalleryViewHolder) {
        Glide.with(holder.imageView).clear(holder.imageView)
    }
}
