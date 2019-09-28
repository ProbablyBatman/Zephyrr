package greenberg.moviedbshell.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.imagegallerymodels.BackdropsItem

class ImagePagerAdapter(
    private val backdrops: List<BackdropsItem>
) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val layout = inflater.inflate(R.layout.image_gallery_single_layout, container, false)
        container.addView(layout)
        val currentItem = backdrops[position]
        // TODO: investigation into this.  I filter out all empty links in the presenter, so...
        if (currentItem.filePath?.isNotEmpty() == true) {
            Glide.with(container.context)
                    .load(container.context.getString(R.string.poster_url_substitution, currentItem.filePath))
                    .apply(
                            RequestOptions()
                                    .centerCrop()
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(layout.findViewById(R.id.image_gallery_current))
        }
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //super.destroyItem(container, position, `object`)
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun getCount() = backdrops.size

}