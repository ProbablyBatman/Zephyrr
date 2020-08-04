package greenberg.moviedbshell.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import greenberg.moviedbshell.R
import greenberg.moviedbshell.viewHolders.GenreViewHolder

class GenreListAdapter(
    var genreList: List<String> = emptyList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GenreViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.genre_item, parent, false))
    }

    override fun getItemCount() = genreList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GenreViewHolder) {
            resetView(holder)
            val currentItem = genreList[position]
            holder.name.text = currentItem
        }
    }

    private fun resetView(holder: GenreViewHolder) {
        holder.name.text = ""
    }
}