package greenberg.moviedbshell.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.adapters.CreditsAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.extensions.processAge
import greenberg.moviedbshell.extensions.processDate
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.PersonDetailState
import greenberg.moviedbshell.state.TvDetailArgs
import greenberg.moviedbshell.viewmodel.PersonDetailViewModel
import timber.log.Timber

class PersonDetailFragment : BaseFragment() {

    val personDetailViewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.personDetailViewModelFactory
    }

    private val viewModel: PersonDetailViewModel by fragmentViewModel()

    private var progressBar: ProgressBar? = null
    private var scrollView: NestedScrollView? = null
    private var posterImageContainer: MaterialCardView? = null
    private var posterImageView: ImageView? = null
    private var name: TextView? = null
    private var birthdayTitle: TextView? = null
    private var birthday: TextView? = null
    private var birthplaceTitle: TextView? = null
    private var birthplace: TextView? = null
    private var deathdayTitle: TextView? = null
    private var deathday: TextView? = null
    private var biography: TextView? = null
    private var creditsRecycler: RecyclerView? = null
    private var creditsAdapter: CreditsAdapter? = null
    private var errorTextView: TextView? = null
    private var errorRetryButton: Button? = null
    private var linearLayoutManager: LinearLayoutManager? = null

    private var personId = -1
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            personId = arguments?.get("PersonID") as? Int ?: -1
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.person_detail_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.person_detail_progress_bar)
        scrollView = view.findViewById(R.id.person_detail_scroll)
        posterImageContainer = view.findViewById(R.id.person_poster_container)
        posterImageView = view.findViewById(R.id.person_poster_image)
        name = view.findViewById(R.id.person_detail_name)
        birthdayTitle = view.findViewById(R.id.person_detail_birthday_title)
        birthday = view.findViewById(R.id.person_detail_birthday)
        birthplaceTitle = view.findViewById(R.id.person_detail_birthplace_title)
        deathday = view.findViewById(R.id.person_detail_deathday)
        deathdayTitle = view.findViewById(R.id.person_detail_deathday_title)
        birthplace = view.findViewById(R.id.person_detail_birthplace)
        biography = view.findViewById(R.id.person_detail_biography)
        errorTextView = view.findViewById(R.id.person_detail_error)
        errorRetryButton = view.findViewById(R.id.person_detail_retry_button)
        creditsRecycler = view.findViewById(R.id.person_detail_recycler)

        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        creditsRecycler?.layoutManager = linearLayoutManager
        creditsAdapter = CreditsAdapter(onClickListener = this::onClickListener)
        creditsRecycler?.adapter = creditsAdapter
        navController = findNavController()
    }

    private fun showLoading() {
        Timber.d("Showing loading")
        hideAllViews()
        hideErrorState()
        showLoadingBar()
    }

    private fun showError(throwable: Throwable) {
        Timber.d("Showing error")
        Timber.e(throwable)
        hideLoadingBar()
        showErrorState()
        errorRetryButton?.setOnClickListener {
            viewModel.fetchPersonDetail()
            hideErrorState()
        }
    }

    private fun showPersonDetails(state: PersonDetailState) {
        val personDetailItem = state.personDetailItem

        if (personDetailItem != null) {
            if (personDetailItem.posterImageUrl.isNotEmpty() && posterImageView != null) {
                val validUrl = resources.getString(
                    R.string.poster_url_substitution,
                    personDetailItem.posterImageUrl
                )
                Glide.with(this)
                    .load(validUrl)
                    .apply(
                        RequestOptions()
                            .placeholder(ColorDrawable(Color.LTGRAY))
                            .fallback(ColorDrawable(Color.LTGRAY))
                            .centerCrop()
                    )
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(posterImageView!!)
            }

            name?.text = personDetailItem.name
            when {
                personDetailItem.deathday.isNotEmpty() -> {
                    birthday?.text = resources.getString(
                        R.string.birthday_no_age_substitution,
                        personDetailItem.birthday.processDate()
                    )
                    deathday?.text = resources.getString(
                        R.string.day_and_age_substitution,
                        personDetailItem.deathday.processDate(),
                        processAge(personDetailItem.birthday, personDetailItem.deathday)
                    )
                    deathday?.visibility = View.VISIBLE
                    deathdayTitle?.visibility = View.VISIBLE
                }
                personDetailItem.birthday.isNotEmpty() -> {
                    birthday?.text = resources.getString(
                        R.string.day_and_age_substitution,
                        personDetailItem.birthday.processDate(),
                        processAge(personDetailItem.birthday)
                    )
                }
                else -> {
                    birthdayTitle?.visibility = View.GONE
                    birthday?.visibility = View.GONE
                }
            }
            if (personDetailItem.placeOfBirth.isNotEmpty()) {
                birthplace?.text = personDetailItem.placeOfBirth
            } else {
                birthplaceTitle?.visibility = View.GONE
                birthplace?.visibility = View.GONE
            }
            biography?.text = personDetailItem.biography

            creditsAdapter?.creditsList = personDetailItem.combinedCredits
            creditsAdapter?.notifyDataSetChanged()
        }
    }

    private fun hideAllViews() {
        progressBar?.visibility = View.GONE
        scrollView?.visibility = View.GONE
        posterImageContainer?.visibility = View.GONE
        posterImageView?.visibility = View.GONE
        name?.visibility = View.GONE
        birthdayTitle?.visibility = View.GONE
        birthday?.visibility = View.GONE
        birthplaceTitle?.visibility = View.GONE
        deathday?.visibility = View.GONE
        deathdayTitle?.visibility = View.GONE
        birthplace?.visibility = View.GONE
        biography?.visibility = View.GONE
        creditsRecycler?.visibility = View.GONE
    }

    private fun showAllViews() {
        scrollView?.visibility = View.VISIBLE
        posterImageContainer?.visibility = View.VISIBLE
        posterImageView?.visibility = View.VISIBLE
        name?.visibility = View.VISIBLE
        birthdayTitle?.visibility = View.VISIBLE
        birthday?.visibility = View.VISIBLE
        birthplaceTitle?.visibility = View.VISIBLE
        birthplace?.visibility = View.VISIBLE
        biography?.visibility = View.VISIBLE
        creditsRecycler?.visibility = View.VISIBLE
    }

    private fun showLoadingBar() {
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        progressBar?.visibility = View.GONE
    }

    private fun hideErrorState() {
        errorTextView?.visibility = View.GONE
        errorRetryButton?.visibility = View.GONE
    }

    private fun showErrorState() {
        errorTextView?.visibility = View.VISIBLE
        errorRetryButton?.visibility = View.VISIBLE
        scrollView?.visibility = View.VISIBLE
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            when (state.personDetailResponse) {
                Uninitialized -> Timber.d("uninitialized")
                is Loading -> {
                    showLoading()
                }
                is Success -> {
                    showPersonDetails(state)
                    hideLoadingBar()
                    showAllViews()
                }
                is Fail -> {
                    showError(state.personDetailResponse.error)
                }
            }
        }
    }

    private fun onClickListener(itemId: Int, mediaType: String) {
        when (mediaType) {
            MediaType.MOVIE -> {
                navigate(
                    R.id.action_personDetailFragment_to_movieDetailFragment,
                    MovieDetailArgs(itemId)
                )
            }
            MediaType.TV -> {
                navigate(
                    R.id.action_personDetailFragment_to_tvDetailFragment,
                    TvDetailArgs(itemId)
                )
            }
        }
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(throwable: Throwable) {
        Timber.e(throwable)
    }
}
