package greenberg.moviedbshell.state

import android.os.Parcelable
import greenberg.moviedbshell.models.ui.CrewMemberItem
import kotlinx.parcelize.Parcelize

data class CrewState(
    val crewMembers: List<CrewMemberItem> = emptyList(),
)

@Parcelize
data class CrewStateArgs(
    val crewMembers: List<CrewMemberItem>,
) : Parcelable
