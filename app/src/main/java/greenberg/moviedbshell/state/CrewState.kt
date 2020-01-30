package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.MvRxState
import kotlinx.android.parcel.Parcelize

data class CrewState(
        val crewMembersJson: String = ""
) : MvRxState  {
    constructor(args: CrewStateArgs) : this(crewMembersJson = args.crewMembersJson)
}

@Parcelize
data class CrewStateArgs(
        val crewMembersJson: String
) : Parcelable