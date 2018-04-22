package greenberg.moviedbshell

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.Models.PopularMoviesModels.PopularMovieResultsItem
import greenberg.moviedbshell.MosbyImpl.MovieDetailFragment

class PopularMovieAdapter(var popularMovieList: MutableList<PopularMovieResultsItem?>?) : RecyclerView.Adapter<PopularMovieAdapter.PopularMovieViewHolder>() {

    override fun onBindViewHolder(holder: PopularMovieViewHolder, position: Int) {
        //todo: load posters and have like, placeholders
        holder.cardItemTitle.text = popularMovieList?.get(position)?.title
        holder.cardItemReleaseDate.text = popularMovieList?.get(position)?.releaseDate?.let { processReleaseDate(it) }
        holder.cardItemOverview.text = popularMovieList?.get(position)?.overview
        popularMovieList?.get(position)?.let { fetchPoster(holder.cardItemPosterImage, it) }
        //TODO: probably move this to the activity
        holder.cardItem.setOnClickListener {
            val fragment = MovieDetailFragment()
            val bundle = Bundle()
            bundle.putInt("MovieID", popularMovieList?.get(position)?.id ?: -1)
            fragment.arguments = bundle
            (it.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .add(fragment, MovieDetailFragment().tag)
                    .commit()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularMovieViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.popular_movie_card, parent, false)
        return PopularMovieViewHolder(v)
    }

    override fun getItemCount() = popularMovieList?.size ?: 0

    //TODO: is this context check ok
    private fun fetchPoster(cardItemPosterView: ImageView, item: PopularMovieResultsItem) {
        //Load poster art
        item.posterPath?.let {
            Glide.with(cardItemPosterView)
                    .load(cardItemPosterView.context.getString(R.string.poster_url_substitution, it))
                    .apply { RequestOptions().centerCrop() }
                    .into(cardItemPosterView)
        }
    }

    //TODO: probably make sure every date is like this?
    //there has to be a better way to do this
    private fun processReleaseDate(releaseDate: String): String {
        val splitDate = releaseDate.split("-")
        return "${splitDate[1]}/${splitDate[2]}/${splitDate[0]}"
    }

    class PopularMovieViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var cardItemPosterImage: ImageView = view.findViewById(R.id.cardItemPosterImage)
        var cardItemTitle: TextView = view.findViewById(R.id.cardItemTitle)
        var cardItemReleaseDate: TextView = view.findViewById(R.id.cardItemReleaseDate)
        var cardItemOverview: TextView = view.findViewById(R.id.cardItemOverview)
        var cardItem: CardView = view.findViewById(R.id.popularMovieCard)
    }
}