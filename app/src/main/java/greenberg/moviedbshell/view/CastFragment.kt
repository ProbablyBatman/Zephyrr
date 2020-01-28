package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import greenberg.moviedbshell.R
import greenberg.moviedbshell.adapters.CastListAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.models.ui.CastMemberItem
import greenberg.moviedbshell.state.CastState
import greenberg.moviedbshell.state.CastStateArgs
import greenberg.moviedbshell.state.PersonDetailArgs
import greenberg.moviedbshell.viewmodel.CastViewModel
import timber.log.Timber

class CastFragment : BaseFragment() {

    private val viewModel: CastViewModel by fragmentViewModel()

    private lateinit var castRecycler: RecyclerView
    private lateinit var castAdapter: CastListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cast_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        castRecycler = view.findViewById(R.id.cast_members_recycler)
        layoutManager = GridLayoutManager(activity, 3)
        castRecycler.layoutManager = layoutManager
        castAdapter = CastListAdapter(onClickListener = this::onClickListener)
        castRecycler.adapter = castAdapter
    }

    private fun showDetails(state: CastState) {
        if (state.castMembersJson.isNotEmpty()) {
            val castList = Gson().fromJson<List<CastMemberItem>>(
                    state.castMembersJson,
                    object : TypeToken<List<CastMemberItem>>() {}.type
            )
            castAdapter.castMemberList = castList
            castAdapter.notifyDataSetChanged()
        }
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            showDetails(state)
        }
    }

    private fun onClickListener(personId: Int) {
        navigate(
                R.id.action_movieDetailFragment_to_personDetailFragment,
                PersonDetailArgs(personId)
        )
    }

    override fun log(message: String) {
        Timber.d(message)
    }

}
