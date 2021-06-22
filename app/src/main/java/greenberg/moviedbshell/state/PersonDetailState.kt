package greenberg.moviedbshell.state

import android.os.Parcelable
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import greenberg.moviedbshell.models.ui.PersonDetailItem
import kotlinx.parcelize.Parcelize

data class PersonDetailState(
    val personId: Int = -1,
    val personDetailItem: PersonDetailItem? = null,
    val personDetailResponse: Async<PersonDetailItem> = Uninitialized
) : MavericksState {
    constructor(args: PersonDetailArgs) : this(personId = args.personId)
}

@Parcelize
data class PersonDetailArgs(
    val personId: Int
) : Parcelable