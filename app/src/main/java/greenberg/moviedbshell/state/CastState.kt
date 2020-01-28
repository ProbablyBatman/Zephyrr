package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.MvRxState
import greenberg.moviedbshell.models.ui.CastMemberItem
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class CastState(
    //val castMembers: List<CastMemberItem> = emptyList()
    val castMembersJson: String = ""
) : MvRxState {
    //constructor(args: CastStateArgs) : this(castMembers = args.castMembers)
    constructor(args: CastStateArgs) : this(castMembersJson = args.castMembersJson)
}

@Parcelize
data class CastStateArgs(
    //val castMembers: List<CastMemberItem>
    val castMembersJson: String
) : Parcelable
