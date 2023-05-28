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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import greenberg.moviedbshell.R
import greenberg.moviedbshell.adapters.CreditsAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.extensions.extractArguments
import greenberg.moviedbshell.extensions.processAge
import greenberg.moviedbshell.extensions.processDate
import greenberg.moviedbshell.models.MediaType
import greenberg.moviedbshell.models.ui.PersonDetailItem
import greenberg.moviedbshell.state.MovieDetailArgs
import greenberg.moviedbshell.state.PersonDetailArgs
import greenberg.moviedbshell.state.PersonDetailState
import greenberg.moviedbshell.state.TvDetailArgs
import greenberg.moviedbshell.viewmodel.PersonDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PersonDetailFragment : BaseFragment() {

    @Inject
    lateinit var personDetailViewModelFactory: PersonDetailViewModel.Factory

    private val viewModel: PersonDetailViewModel by viewModels {
        PersonDetailViewModel.provideFactory(
            personDetailViewModelFactory,
            arguments?.extractArguments<PersonDetailArgs>(PAGE_ARGS)?.personId ?: -1,
            Dispatchers.IO
        )
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var scrollView: NestedScrollView
    private lateinit var posterImageContainer: MaterialCardView
    private lateinit var posterImageView: ImageView
    private lateinit var name: TextView
    private lateinit var birthdayTitle: TextView
    private lateinit var birthday: TextView
    private lateinit var birthplaceTitle: TextView
    private lateinit var birthplace: TextView
    private lateinit var deathdayTitle: TextView
    private lateinit var deathday: TextView
    private lateinit var biography: TextView
    private lateinit var creditsRecycler: RecyclerView
    private lateinit var creditsAdapter: CreditsAdapter
    private lateinit var errorTextView: TextView
    private lateinit var errorRetryButton: Button
    private lateinit var linearLayoutManager: LinearLayoutManager

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        creditsRecycler.layoutManager = linearLayoutManager
        creditsAdapter = CreditsAdapter(onClickListener = this::onClickListener)
        creditsRecycler.adapter = creditsAdapter
        navController = findNavController()

        registerObservers()
    }

    private fun registerObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.personDetailState.collect {
                        updatePersonDetails(it)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        log("Showing loading")
        hideAllViews()
        hideErrorState()
        showLoadingBar()
    }

    private fun showError(throwable: Throwable) {
        log("Showing error")
        log(throwable)
        hideLoadingBar()
        showErrorState()
        errorRetryButton.setOnClickListener {
            viewModel.fetchPersonDetail()
            hideErrorState()
        }
    }

    private fun showPersonDetails(personDetailItem: PersonDetailItem) {
        if (personDetailItem.posterImageUrl.isNotEmpty()) {
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
                .into(posterImageView)
        }

        name.text = personDetailItem.name
        when {
            personDetailItem.deathday.isNotEmpty() -> {
                birthday.text = resources.getString(
                    R.string.birthday_no_age_substitution,
                    personDetailItem.birthday.processDate()
                )
                deathday.text = resources.getString(
                    R.string.day_and_age_substitution,
                    personDetailItem.deathday.processDate(),
                    processAge(personDetailItem.birthday, personDetailItem.deathday)
                )
                deathday.visibility = View.VISIBLE
                deathdayTitle.visibility = View.VISIBLE
            }
            personDetailItem.birthday.isNotEmpty() -> {
                birthday.text = resources.getString(
                    R.string.day_and_age_substitution,
                    personDetailItem.birthday.processDate(),
                    processAge(personDetailItem.birthday)
                )
            }
            else -> {
                birthdayTitle.visibility = View.GONE
                birthday.visibility = View.GONE
            }
        }
        if (personDetailItem.placeOfBirth.isNotEmpty()) {
            birthplace.text = personDetailItem.placeOfBirth
        } else {
            birthplaceTitle.visibility = View.GONE
            birthplace.visibility = View.GONE
        }
        biography.text = personDetailItem.biography

        creditsAdapter.creditsList = personDetailItem.combinedCredits
        creditsAdapter.notifyItemRangeChanged(0, creditsAdapter.itemCount)
    }

    private fun hideAllViews() {
        progressBar.visibility = View.GONE
        scrollView.visibility = View.GONE
        posterImageContainer.visibility = View.GONE
        posterImageView.visibility = View.GONE
        name.visibility = View.GONE
        birthdayTitle.visibility = View.GONE
        birthday.visibility = View.GONE
        birthplaceTitle.visibility = View.GONE
        deathday.visibility = View.GONE
        deathdayTitle.visibility = View.GONE
        birthplace.visibility = View.GONE
        biography.visibility = View.GONE
        creditsRecycler.visibility = View.GONE
    }

    private fun showAllViews() {
        scrollView.visibility = View.VISIBLE
        posterImageContainer.visibility = View.VISIBLE
        posterImageView.visibility = View.VISIBLE
        name.visibility = View.VISIBLE
        birthdayTitle.visibility = View.VISIBLE
        birthday.visibility = View.VISIBLE
        birthplaceTitle.visibility = View.VISIBLE
        birthplace.visibility = View.VISIBLE
        biography.visibility = View.VISIBLE
        creditsRecycler.visibility = View.VISIBLE
    }

    private fun showLoadingBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        progressBar.visibility = View.GONE
    }

    private fun hideErrorState() {
        errorTextView.visibility = View.GONE
        errorRetryButton.visibility = View.GONE
    }

    private fun showErrorState() {
        errorTextView.visibility = View.VISIBLE
        errorRetryButton.visibility = View.VISIBLE
        scrollView.visibility = View.VISIBLE
    }

    private fun updatePersonDetails(state: PersonDetailState) {
        when {
            state.isLoading -> showLoading()
            state.error != null -> showError(state.error)
            state.personDetailItem != null -> {
                showPersonDetails(state.personDetailItem)
                hideLoadingBar()
                showAllViews()
            }
        }
    }

    private fun onClickListener(itemId: Int, mediaType: MediaType) {
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

            else -> {
                // TODO: fix this no-op?
                // no-op
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
