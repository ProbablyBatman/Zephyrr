package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import greenberg.moviedbshell.R
import greenberg.moviedbshell.adapters.CrewListAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.extensions.extractArguments
import greenberg.moviedbshell.state.CrewState
import greenberg.moviedbshell.state.CrewStateArgs
import greenberg.moviedbshell.state.PersonDetailArgs
import greenberg.moviedbshell.viewmodel.CrewViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class CrewFragment : BaseFragment() {

    // TODO: error handling for empty list?
    private val viewModel: CrewViewModel by viewModels {
        CrewViewModel.provideFactory(arguments?.extractArguments<CrewStateArgs>(PAGE_ARGS)?.crewMembers ?: emptyList())
    }

    private lateinit var crewRecycler: RecyclerView
    private lateinit var crewAdapter: CrewListAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.crew_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crewRecycler = view.findViewById(R.id.crew_members_recycler)
        layoutManager = LinearLayoutManager(activity)
        crewRecycler.layoutManager = layoutManager
        crewAdapter = CrewListAdapter(onClickListener = this::onClickListener)
        crewRecycler.adapter = crewAdapter
        registerObservers()
    }

    private fun registerObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.crewMemberState.collect {
                        showDetails(it)
                    }
                }
            }
        }
    }

    private fun showDetails(state: CrewState) {
        if (state.crewMembers.isNotEmpty()) {
            crewAdapter.setMembersList(state.crewMembers)
        }
    }

    private fun onClickListener(personId: Int) {
        navigate(
            R.id.action_crewFragment_to_personDetailFragment,
            PersonDetailArgs(personId),
        )
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }
}
