package greenberg.moviedbshell.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.R
import greenberg.moviedbshell.extensions.processAsReleaseDate
import greenberg.moviedbshell.extensions.processKnownForItems
import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.models.ui.PersonItem
import greenberg.moviedbshell.models.ui.PreviewItem
import greenberg.moviedbshell.models.ui.TvItem
import greenberg.moviedbshell.viewHolders.SearchResultsViewHolder

class SearchResultsAdapter(
    var searchResults: List<PreviewItem> = listOf(),
    val onClickListener: (Int, String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SearchResultsViewHolder) {
            // Reset text, images, and listeners
            resetView(holder)
            when (val currentItem = searchResults[position]) {
                is MovieItem -> {
                    holder.searchItemTitle.text = currentItem.movieTitle
                    holder.searchItemSubInfo.text = currentItem.releaseDate.processAsReleaseDate()
                    holder.searchItemOverview.text = currentItem.overview
                    fetchPosterArt(holder.searchItemPosterImage, currentItem.posterImageUrl)
                    holder.cardItem.setOnClickListener {
                        onClickListener(
                            currentItem.id ?: -1,
                            currentItem.mediaType
                        )
                    }
                }
                is TvItem -> {
                    holder.searchItemTitle.text = currentItem.name
                    holder.searchItemSubInfo.text = currentItem.firstAirDate.processAsReleaseDate()
                    holder.searchItemOverview.text = currentItem.overview
                    fetchPosterArt(holder.searchItemPosterImage, currentItem.posterImageUrl)
                    holder.cardItem.setOnClickListener {
                        onClickListener(
                            currentItem.id ?: -1,
                            currentItem.mediaType
                        )
                    }
                }
                is PersonItem -> {
                    holder.searchItemTitle.text = currentItem.name
                    // Remove this in favor of showing known for items
                    holder.searchItemSubInfo.visibility = View.GONE
                    val knownForText = currentItem.knownForItems.processKnownForItems()
                    if (knownForText.isNotEmpty()) {
                        holder.searchItemOverview.text = knownForText
                    } else {
                        holder.searchItemOverview.visibility = View.GONE
                    }
                    fetchPosterArt(holder.searchItemPosterImage, currentItem.posterImageUrl)
                    holder.cardItem.setOnClickListener {
                        onClickListener(
                            currentItem.id ?: -1,
                            currentItem.mediaType
                        )
                    }
                }
                else -> {
                    // TODO: handle unknown type?
                    // Can any information be assumed?  For now place unknown
                }
            }
        }
    }

    override fun getItemCount() = searchResults.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchResultsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_card_layout, parent, false))
    }

    private fun fetchPosterArt(cardItemPosterView: ImageView, posterImageUrl: String) {
        Glide.with(cardItemPosterView).clear(cardItemPosterView)
        // Load poster art
        if (posterImageUrl.isNotEmpty()) {
            Glide.with(cardItemPosterView)
                .load(
                    cardItemPosterView.context.getString(
                        R.string.poster_url_substitution,
                        posterImageUrl
                    )
                )
                .apply(
                    RequestOptions()
                        .placeholder(ColorDrawable(Color.LTGRAY))
                        .fallback(ColorDrawable(Color.LTGRAY))
                        .centerCrop()
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(cardItemPosterView)
        }
    }

    private fun resetView(holder: SearchResultsViewHolder) {
        holder.searchItemTitle.text = ""
        holder.searchItemSubInfo.text = ""
        holder.searchItemOverview.text = ""
        holder.cardItem.setOnClickListener(null)
        Glide.with(holder.searchItemPosterImage).clear(holder.searchItemPosterImage)
    }
}
