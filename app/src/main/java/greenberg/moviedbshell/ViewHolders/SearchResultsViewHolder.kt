package greenberg.moviedbshell.ViewHolders

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.Models.SearchModels.SearchResultsItem
import greenberg.moviedbshell.R
import java.text.SimpleDateFormat

class SearchResultsAdapter(var searchResults: MutableList<SearchResultsItem?>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SearchResultsViewHolder) {
            val currentItem = searchResults?.get(position)
            when (currentItem?.mediaType) {
                MEDIA_TYPE_MOVIE -> {
                    holder.searchItemTitle.text = currentItem.title
                    holder.searchItemSubInfo.text = currentItem.releaseDate?.let { processReleaseDate(it) }
                    holder.searchItemOverview.text = currentItem.overview
                    fetchPoster(holder.searchItemPosterImage, currentItem)
                }
                MEDIA_TYPE_TV -> {
                    holder.searchItemTitle.text = currentItem.title
                    holder.searchItemSubInfo.text = currentItem.firstAirDate?.let { processReleaseDate(it) }
                    holder.searchItemOverview.text = currentItem.overview
                    fetchPoster(holder.searchItemPosterImage, currentItem)
                }
                MEDIA_TYPE_PERSON -> {
                    //One of the problems with people is their limited fields.  Currently they only have:
                    //popularity, media type, id, profile path (for picture), name, and known for (list of movies).
                    holder.searchItemTitle.text = currentItem.name
                    //todo: find better way to do these
                    holder.searchItemSubInfo.text = ""
                    holder.searchItemOverview.text = ""
                    fetchPoster(holder.searchItemPosterImage, currentItem)
                }
                else -> {
                    //TODO: handle unknown type?
                    //Can any information be assumed?  For now place unknown
                }
            }
        }
    }

    override fun getItemCount() = searchResults?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchResultsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_card_layout, parent, false))
    }

    //TODO: is this context check ok
    //TODO: Really look into getting rid of these types of functions from here
    private fun fetchPoster(cardItemPosterView: ImageView, item: SearchResultsItem) {
        //Load poster art
        when (item.mediaType) {
            MEDIA_TYPE_MOVIE, MEDIA_TYPE_TV -> {
                item.posterPath
            }
            MEDIA_TYPE_PERSON -> {
                item.profilePath
            }
            else -> {
                //Don't fetch poster if there is no poster art
                return
            }
        }?.let {
            Glide.with(cardItemPosterView)
                    .load(cardItemPosterView.context.getString(R.string.poster_url_substitution, it))
                    .apply { RequestOptions().centerCrop() }
                    .into(cardItemPosterView)
        }
    }

    //TODO: probably make sure every date is like this?
    //there has to be a better way to do this
    private fun processReleaseDate(releaseDate: String): String {
        return if (releaseDate.isNotBlank()) {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = inputFormat.parse(releaseDate)
            val outputFormat = SimpleDateFormat("MM/dd/yyyy")
            outputFormat.format(date)
        } else {
            return ""
        }
    }


    class SearchResultsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var searchItemPosterImage: ImageView = view.findViewById(R.id.search_item_poster_image)
        var searchItemTitle: TextView = view.findViewById(R.id.search_item_title)
        //Used for release date, or alternatively info line of "Actor"
        var searchItemSubInfo: TextView = view.findViewById(R.id.search_item_sub_info_line)
        //Used for movies, blank for actors.
        //TODO: potentially add well known movies provided by api to this
        var searchItemOverview: TextView = view.findViewById(R.id.search_item_overview)
    }

    companion object {
        const val MEDIA_TYPE_PERSON = "person"
        const val MEDIA_TYPE_MOVIE = "movie"
        const val MEDIA_TYPE_TV = "tv"
    }
}