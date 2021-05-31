package greenberg.moviedbshell.models.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class CrewMemberItem(
    open val job: String,
    open val name: String,
    open val posterUrl: String,
    open val id: Int?
) : Parcelable

@Parcelize
data class CollapsedCrewMemberItem(
    var job: String,
    val name: String,
    val posterUrl: String,
    val id: Int?
) : Parcelable
