package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import greenberg.moviedbshell.R
import greenberg.moviedbshell.adapters.ProductionDetailAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.extensions.processAsReleaseDate
import greenberg.moviedbshell.extensions.processRuntime
import greenberg.moviedbshell.state.PersonDetailArgs
import greenberg.moviedbshell.state.ProductionDetailState
import greenberg.moviedbshell.viewmodel.ProductionDetailViewModel
import timber.log.Timber
import java.text.NumberFormat
import java.util.Locale

class ProductionDetailFragment : BaseFragment() {

    private val viewModel: ProductionDetailViewModel by fragmentViewModel()

    private lateinit var productionCompaniesList: TextView
    private lateinit var filmingLocationsList: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var runtimeTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var budgetTextView: TextView
    private lateinit var revenueTextView: TextView
    private lateinit var productionDetailRecycler: RecyclerView
    private lateinit var productionDetailAdapter: ProductionDetailAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.production_detail_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productionCompaniesList = view.findViewById(R.id.detail_production_companies)
        filmingLocationsList = view.findViewById(R.id.detail_filming_locations)
        releaseDateTextView = view.findViewById(R.id.detail_release_date)
        runtimeTextView = view.findViewById(R.id.detail_runtime)
        statusTextView = view.findViewById(R.id.detail_status)
        budgetTextView = view.findViewById(R.id.detail_budget)
        revenueTextView = view.findViewById(R.id.detail_revenue)
        productionDetailRecycler = view.findViewById(R.id.production_detail_recycler)
        productionDetailAdapter = ProductionDetailAdapter(onClickListener = this::onClickListener)
        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        productionDetailRecycler.apply {
            adapter = productionDetailAdapter
            layoutManager = linearLayoutManager
        }
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            showDetails(state)
        }
    }

    private fun showDetails(state: ProductionDetailState) {
        val detailItem = state.productionDetailItem
        productionCompaniesList.text = detailItem.productionCompanies.joinToString(", ") { it.name }
        filmingLocationsList.text = detailItem.productionCountries.joinToString(", ") { it.name }
        releaseDateTextView.text = detailItem.releaseDate.processAsReleaseDate()
        runtimeTextView.text = requireContext().processRuntime(detailItem.runtime)
        statusTextView.text = detailItem.status
        budgetTextView.text = resources.getString(R.string.budget_substitution, NumberFormat.getInstance(Locale.US).format(detailItem.budget))
        revenueTextView.text = resources.getString(R.string.budget_substitution, NumberFormat.getInstance(Locale.US).format(detailItem.revenue))
        productionDetailAdapter.crewMemberList = detailItem.crewMembers
        productionDetailAdapter.notifyDataSetChanged()
    }

    // TODO: might have to make recycler cards here instead of just letting them tap?
    private fun onClickListener(personId: Int) {
        navigate(
            R.id.action_productionDetailFragment_to_personDetailFragment,
            PersonDetailArgs(personId)
        )
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }
}
