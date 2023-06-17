package greenberg.moviedbshell.models.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class CastMemberItem(
    open val role: String,
    open val name: String,
    open val posterUrl: String,
    open val id: Int?,
) : Parcelable
