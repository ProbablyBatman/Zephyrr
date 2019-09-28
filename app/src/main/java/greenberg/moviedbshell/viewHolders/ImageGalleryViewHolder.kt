package greenberg.moviedbshell.viewHolders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import greenberg.moviedbshell.R

class ImageGalleryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var imageView: ImageView = view.findViewById(R.id.image_gallery_current)
}