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
import greenberg.moviedbshell.extensions.processAsReleaseDate
import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.viewHolders.PopularMovieViewHolder

class PopularMovieAdapter(
    var popularMovieList: List<MovieItem> = listOf(),
    val onClickListener : (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PopularMovieViewHolder) {
            resetView(holder)
            val currentItem = popularMovieList[position]
            // todo: load posters and have like, placeholders
            if (currentItem.posterImageUrl.isNotEmpty()) {
                Glide.with(holder.cardItemPosterImage)
                        .load(holder.cardItemPosterImage.context.getString(R.string.poster_url_substitution, currentItem.posterImageUrl))
                        .apply(
                            RequestOptions()
                                    .placeholder(ColorDrawable(Color.LTGRAY))
                                    .fallback(ColorDrawable(Color.LTGRAY))
                                    .centerCrop()
                        )
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.cardItemPosterImage)
            }
            holder.cardItemTitle.text = currentItem.movieTitle
            holder.cardItemReleaseDate.text = currentItem.releaseDate.processAsReleaseDate()
            holder.cardItemOverview.text = currentItem.overview
            holder.cardItem.setOnClickListener { onClickListener(currentItem.id ?: -1) }
        }
    }

    override fun getItemCount() = popularMovieList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PopularMovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.popular_movie_card, parent, false))
    }

    private fun resetView(holder: PopularMovieViewHolder) {
        holder.cardItemTitle.text = ""
        holder.cardItemReleaseDate.text = ""
        holder.cardItemOverview.text = ""
        holder.cardItem.setOnClickListener(null)
        Glide.with(holder.cardItemPosterImage).clear(holder.cardItemPosterImage)
    }
}
