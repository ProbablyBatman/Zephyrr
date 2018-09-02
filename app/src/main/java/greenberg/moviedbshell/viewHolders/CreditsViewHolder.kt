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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.R
import greenberg.moviedbshell.models.ui.PersonDetailCreditItem
import greenberg.moviedbshell.presenters.PersonDetailPresenter

class CreditsAdapter(var creditsList: MutableList<PersonDetailCreditItem> = mutableListOf(),
                     private val presenter: PersonDetailPresenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CreditsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.credit_list_card, parent, false))
    }

    override fun getItemCount() = creditsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CreditsViewHolder) {
            resetView(holder)
            val currentItem = creditsList[position]
            if (currentItem.posterImageUrl.isNotEmpty()) {
                Glide.with(holder.actorImage)
                        .load(holder.actorImage.context.getString(R.string.poster_url_substitution, currentItem.posterImageUrl))
                        .apply {
                            RequestOptions()
                                    .placeholder(ColorDrawable(Color.DKGRAY))
                                    .fallback(ColorDrawable(Color.DKGRAY))
                                    .centerCrop()
                        }
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(holder.actorImage)
            }
            holder.itemTitle.text = currentItem.title
            holder.releaseDate.text = presenter.processDate(currentItem.releaseDate)
            holder.actorRole.text = currentItem.role
            holder.cardView.setOnClickListener { presenter.onCardSelected(currentItem.id ?: -1, currentItem.mediaType) }
        }
    }

    private fun resetView(holder: CreditsViewHolder) {
        Glide.with(holder.actorImage).clear(holder.actorImage)
        holder.itemTitle.text = ""
        holder.releaseDate.text = ""
        holder.actorRole.text = ""
        holder.cardView.setOnClickListener(null)
    }

    class CreditsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.person_detail_card)
        val actorImage: ImageView = view.findViewById(R.id.credit_list_item_image)
        val itemTitle: TextView = view.findViewById(R.id.credit_list_item_title)
        val releaseDate: TextView = view.findViewById(R.id.credit_list_item_release_date)
        val actorRole: TextView = view.findViewById(R.id.credit_list_item_role)
    }
}