package greenberg.moviedbshell.models.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class JobItem(
    val job: String,
    val episodeCount: Int
) : Parcelable
