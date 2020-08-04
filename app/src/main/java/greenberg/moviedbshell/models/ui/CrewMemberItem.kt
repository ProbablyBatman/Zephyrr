package greenberg.moviedbshell.models.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CrewMemberItem(
    val job: String,
    val name: String,
    val posterUrl: String,
    val id: Int?
) : Parcelable