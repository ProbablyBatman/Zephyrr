package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.MavericksState
import greenberg.moviedbshell.models.ui.CrewMemberItem
import kotlinx.parcelize.Parcelize

data class CrewState(
    val crewMembers: List<CrewMemberItem> = emptyList()
) : MavericksState {
    constructor(args: CrewStateArgs) : this(crewMembers = args.crewMembers)
}

@Parcelize
data class CrewStateArgs(
    val crewMembers: List<CrewMemberItem>
) : Parcelable
