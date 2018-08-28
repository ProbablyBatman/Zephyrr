package greenberg.moviedbshell.viewHolders

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.ui.CastItem

class CastListAdapter(var castList: MutableList<CastItem> = mutableListOf()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CastListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cast_list_card, parent, false))
    }

    override fun getItemCount() = castList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CastListViewHolder) {
            resetView(holder)
            val currentItem = castList[position]
            holder.actorRole.text = currentItem.role
            holder.actorName.text = currentItem.name
            //Set poster image
            val validUrl = holder.actorImage.context.getString(R.string.poster_url_substitution, currentItem.posterUrl)
            if (validUrl.isNotEmpty()) {
                Glide.with(holder.actorImage)
                        .load(validUrl)
                        .apply {
                            RequestOptions()
                                    .placeholder(ColorDrawable(Color.DKGRAY))
                                    .fallback(ColorDrawable(Color.DKGRAY))
                                    .centerCrop()
                        }
                        .into(holder.actorImage)
            }
        }
    }

    private fun resetView(holder: CastListViewHolder) {
        holder.actorRole.text = ""
        holder.actorName.text = ""
        Glide.with(holder.actorImage).clear(holder.actorImage)
    }

    class CastListViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var actorImage: ImageView = view.findViewById(R.id.cast_list_item_image)
        var actorRole: TextView = view.findViewById(R.id.cast_list_item_role)
        var actorName: TextView = view.findViewById(R.id.cast_list_item_actor)
    }
}