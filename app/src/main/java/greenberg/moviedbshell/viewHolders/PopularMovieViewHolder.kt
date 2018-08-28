package greenberg.moviedbshell.viewHolders

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.presenters.PopularMoviesPresenter
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.ui.MovieItem

class PopularMovieAdapter(var popularMovieList: MutableList<MovieItem> = mutableListOf(),
                          private val presenter: PopularMoviesPresenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PopularMovieViewHolder) {
            resetView(holder)
            val currentItem = popularMovieList[position]
            //todo: load posters and have like, placeholders
            if (currentItem.posterImageUrl.isNotEmpty()) {
                Glide.with(holder.cardItemPosterImage)
                        .load(holder.cardItemPosterImage.context.getString(R.string.poster_url_substitution, currentItem.posterImageUrl))
                        .apply {
                            RequestOptions()
                                    .placeholder(ColorDrawable(Color.DKGRAY))
                                    .fallback(ColorDrawable(Color.DKGRAY))
                                    .centerCrop()
                        }
                        .into(holder.cardItemPosterImage)
            }
            holder.cardItemTitle.text = currentItem.movieTitle
            holder.cardItemReleaseDate.text = presenter.processReleaseDate(currentItem.releaseDate)
            holder.cardItemOverview.text = currentItem.overview
            holder.cardItem.setOnClickListener { presenter.onCardSelected(currentItem.id ?: -1) }
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

    class PopularMovieViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var cardItemPosterImage: ImageView = view.findViewById(R.id.cardItemPosterImage)
        var cardItemTitle: TextView = view.findViewById(R.id.cardItemTitle)
        var cardItemReleaseDate: TextView = view.findViewById(R.id.cardItemReleaseDate)
        var cardItemOverview: TextView = view.findViewById(R.id.cardItemOverview)
        var cardItem: CardView = view.findViewById(R.id.popularMovieCard)
    }
}
