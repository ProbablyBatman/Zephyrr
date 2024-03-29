package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.MavericksState
import greenberg.moviedbshell.models.ui.CastMemberItem
import kotlinx.parcelize.Parcelize

data class CastState(
    val castMembers: List<CastMemberItem> = emptyList()
) : MavericksState {
    constructor(args: CastStateArgs) : this(castMembers = args.castMembers)
}

@Parcelize
data class CastStateArgs(
    val castMembers: List<CastMemberItem>
) : Parcelable
