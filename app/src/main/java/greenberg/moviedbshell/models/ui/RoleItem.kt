package greenberg.moviedbshell.models.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RoleItem(
    val character: String,
    val episodeCount: Int
) : Parcelable
