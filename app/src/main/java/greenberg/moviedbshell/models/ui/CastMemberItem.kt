package greenberg.moviedbshell.models.ui

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CastMemberItem(
    val role: String,
    val name: String,
    val posterUrl: String,
    val id: Int?
) : Parcelable