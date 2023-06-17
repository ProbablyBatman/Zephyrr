package greenberg.moviedbshell.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.ui.PersonItem

class MultiSearchQueryItemsAdapter(
    var currentQueries: List<PersonItem> = listOf(),
    val removeOnClickListener: (Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MultiSearchQueryItemsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.multi_search_query_item, parent, false),
        )
    }

    override fun getItemCount() = currentQueries.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MultiSearchQueryItemsViewHolder) {
            resetView(holder)
            // TODO: change this to be 2 parallel lists for Ids and names?
            holder.queryText.text = currentQueries[position].name
            holder.removeQueryImage.setOnClickListener { removeOnClickListener(position) }
        }
    }

    internal class MultiSearchQueryItemsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val queryText: TextView = view.findViewById(R.id.multi_search_query_text)
        val removeQueryImage: ImageView = view.findViewById(R.id.multi_search_query_remove)
    }

    private fun resetView(holder: MultiSearchQueryItemsViewHolder) {
        holder.queryText.text = ""
        holder.removeQueryImage.setOnClickListener(null)
    }
}
