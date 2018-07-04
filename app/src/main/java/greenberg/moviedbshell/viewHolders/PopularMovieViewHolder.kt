package greenberg.moviedbshell.viewHolders

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import greenberg.moviedbshell.models.PopularMoviesModels.PopularMovieResultsItem
import greenberg.moviedbshell.mosbyImpl.PopularMoviesPresenter
import greenberg.moviedbshell.R

class PopularMovieAdapter(var popularMovieList: MutableList<PopularMovieResultsItem?> = mutableListOf(),
                          private val presenter: PopularMoviesPresenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PopularMovieViewHolder) {
            val currentItem = popularMovieList.getOrNull(position)
            //todo: load posters and have like, placeholders
            holder.cardItemTitle.text = currentItem?.title
            holder.cardItemReleaseDate.text = currentItem?.releaseDate?.let { presenter.processReleaseDate(it) }
            holder.cardItemOverview.text = currentItem?.overview
            popularMovieList[position]?.let { presenter.fetchPosterArt(holder.cardItemPosterImage, it) }
            holder.cardItem.setOnClickListener { presenter.onCardSelected(currentItem?.id ?: -1) }
        }
    }

    override fun getItemCount() = popularMovieList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PopularMovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.popular_movie_card, parent, false))
    }

    class PopularMovieViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var cardItemPosterImage: ImageView = view.findViewById(R.id.cardItemPosterImage)
        var cardItemTitle: TextView = view.findViewById(R.id.cardItemTitle)
        var cardItemReleaseDate: TextView = view.findViewById(R.id.cardItemReleaseDate)
        var cardItemOverview: TextView = view.findViewById(R.id.cardItemOverview)
        var cardItem: CardView = view.findViewById(R.id.popularMovieCard)
    }
}
