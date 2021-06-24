package greenberg.moviedbshell.viewHolders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import greenberg.moviedbshell.R

class LandingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    var poster = view.findViewById<ImageView>(R.id.landing_poster)
}
