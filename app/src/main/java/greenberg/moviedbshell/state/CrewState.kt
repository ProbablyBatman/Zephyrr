package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.MvRxState
import greenberg.moviedbshell.models.ui.CrewMemberItem
import kotlinx.parcelize.Parcelize

data class CrewState(
    val crewMembers: List<CrewMemberItem> = emptyList()
) : MvRxState {
    constructor(args: CrewStateArgs) : this(crewMembers = args.crewMembers)
}

@Parcelize
data class CrewStateArgs(
    val crewMembers: List<CrewMemberItem>
) : Parcelable
