package greenberg.moviedbshell.viewHolders

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.ui.CastItem
import greenberg.moviedbshell.presenters.TvDetailPresenter

class CastListAdapter(var castList: MutableList<CastItem> = mutableListOf(),
                      private val presenter: TvDetailPresenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  CastListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cast_list_card, parent, false))
    }

    override fun getItemCount() = castList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CastListViewHolder) {
            val currentItem = castList[position]
            holder.actorRole.text = currentItem.role
            holder.actorName.text = currentItem.name
            //Set poster image
            presenter.fetchPosterArt(holder.actorImage, currentItem.posterUrl)
        }
    }

    class CastListViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var actorImage: ImageView = view.findViewById(R.id.cast_list_item_image)
        var actorRole: TextView = view.findViewById(R.id.cast_list_item_role)
        var actorName: TextView = view.findViewById(R.id.cast_list_item_actor)
    }
}