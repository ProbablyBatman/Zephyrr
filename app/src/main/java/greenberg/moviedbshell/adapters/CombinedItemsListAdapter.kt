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
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.models.ui.MovieItem
import greenberg.moviedbshell.models.ui.PreviewItem
import greenberg.moviedbshell.models.ui.TvItem
import greenberg.moviedbshell.viewHolders.MovieGridViewHolder
import greenberg.moviedbshell.viewHolders.MovieListViewHolder

// TODO: just keep using the movie one for now, but update this to be renamed and agnostic later
class CombinedItemsListAdapter(
    var items: List<PreviewItem> = emptyList(),
    val onClickListener: (Int, MediaType) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var currentViewType = ViewType.VIEW_TYPE_GRID

    enum class ViewType {
        VIEW_TYPE_GRID, VIEW_TYPE_LIST
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ViewType.VIEW_TYPE_GRID.ordinal -> {
                val view = inflater.inflate(R.layout.movie_grid_item, parent, false)
                MovieGridViewHolder(view)
            }
            ViewType.VIEW_TYPE_LIST.ordinal -> {
                val view = inflater.inflate(R.layout.movie_list_card, parent, false)
                MovieListViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.movie_grid_item, parent, false)
                MovieGridViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = items[position]
        resetView(holder)
        when (currentViewType) {
            ViewType.VIEW_TYPE_GRID -> bindGridItem(holder, currentItem)
            ViewType.VIEW_TYPE_LIST -> bindListItem(holder, currentItem)
        }
    }

    // TODO: questionable
    override fun getItemViewType(position: Int) = currentViewType.ordinal

    private fun bindGridItem(holder: RecyclerView.ViewHolder, item: PreviewItem) {
        if (holder is MovieGridViewHolder) {
            if (item.posterImageUrl.isNotEmpty()) {
                Glide.with(holder.poster)
                    .load(holder.poster.context.getString(R.string.poster_url_substitution, item.posterImageUrl))
                    .apply(
                        RequestOptions()
                            .placeholder(ColorDrawable(Color.LTGRAY))
                            .fallback(ColorDrawable(Color.LTGRAY))
                            .centerCrop()
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.poster)
            }
            holder.itemView.setOnClickListener { onClickListener(item.id ?: -1, item.mediaType) }
        }
    }

    // Fix the types here
    private fun bindListItem(holder: RecyclerView.ViewHolder, item: PreviewItem) {
        if (holder is MovieListViewHolder) {
            holder.title.text = when (item) {
                is MovieItem -> item.movieTitle
                is TvItem -> item.name
                else -> ""
            }
            holder.overview.text = when (item) {
                is MovieItem -> item.overview
                is TvItem -> item.overview
                else -> ""
            }
            if (item.posterImageUrl.isNotEmpty()) {
                Glide.with(holder.poster)
                    .load(holder.poster.context.getString(R.string.poster_url_substitution, item.posterImageUrl))
                    .apply(
                        RequestOptions()
                            .placeholder(ColorDrawable(Color.LTGRAY))
                            .fallback(ColorDrawable(Color.LTGRAY))
                            .centerCrop()
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.poster)
            }
            holder.itemView.setOnClickListener { onClickListener(item.id ?: -1, item.mediaType) }
        }
    }

    fun updateViewType(viewType: ViewType) {
        currentViewType = viewType
        notifyDataSetChanged()
    }

    fun switchViewType(): ViewType {
        currentViewType = if (currentViewType == ViewType.VIEW_TYPE_GRID) {
            ViewType.VIEW_TYPE_LIST
        } else {
            ViewType.VIEW_TYPE_GRID
        }
        notifyDataSetChanged()
        return currentViewType
    }

    private fun resetView(holder: RecyclerView.ViewHolder) {
        when (currentViewType) {
            ViewType.VIEW_TYPE_GRID -> {
                if (holder is MovieGridViewHolder) {
                    Glide.with(holder.poster).clear(holder.poster)
                }
            }
            ViewType.VIEW_TYPE_LIST -> {
                if (holder is MovieListViewHolder) {
                    holder.title.text = ""
                    holder.overview.text = ""
                    Glide.with(holder.poster).clear(holder.poster)
                }
            }
        }
        holder.itemView.setOnClickListener(null)
    }
}
