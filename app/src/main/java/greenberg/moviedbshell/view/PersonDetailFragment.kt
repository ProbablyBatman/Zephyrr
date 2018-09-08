package greenberg.moviedbshell.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.models.ui.PersonDetailItem
import greenberg.moviedbshell.presenters.PersonDetailPresenter
import greenberg.moviedbshell.presenters.SearchPresenter
import greenberg.moviedbshell.viewHolders.CreditsAdapter
import timber.log.Timber

class PersonDetailFragment :
        BaseFragment<PersonDetailView, PersonDetailPresenter>(),
        PersonDetailView {

    private var progressBar: ProgressBar? = null
    private var scrollView: NestedScrollView? = null
    private var posterImageContainer: CardView? = null
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
        creditsRecycler = view.findViewById(R.id.person_detail_recycler)

        linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        creditsRecycler?.layoutManager = linearLayoutManager
        creditsAdapter = CreditsAdapter(presenter = presenter)
        creditsRecycler?.adapter = creditsAdapter

        presenter.initView(creditsAdapter)
        presenter.loadPersonDetails(personId)
        navController = findNavController()
    }

    override fun createPresenter() = presenter
            ?: (activity?.application as ZephyrrApplication).component.personDetailPresenter()

    override fun showLoading() {
        Timber.d("Showing loading")
        hideAllViews()
        toggleLoading()
    }

    override fun showError(throwable: Throwable) {
        Timber.d("Showing error")
        Timber.d(throwable)
    }

    override fun showPersonDetails(personDetailItem: PersonDetailItem) {
        toggleLoading()
        showAllViews()

        if (personDetailItem.posterImageUrl.isNotEmpty() && posterImageView != null) {
            val validUrl = resources.getString(R.string.poster_url_substitution, personDetailItem.posterImageUrl)
            Glide.with(this)
                    .load(validUrl)
                    .apply {
                        RequestOptions()
                                .placeholder(ColorDrawable(Color.DKGRAY))
                                .fallback(ColorDrawable(Color.DKGRAY))
                                .centerCrop()
                    }
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(posterImageView!!)
        }

        name?.text = personDetailItem.name
        when {
            personDetailItem.deathday.isNotEmpty() -> {
                birthday?.text = resources.getString(R.string.birthday_no_age_substitution,
                        presenter.processDate(personDetailItem.birthday))
                deathday?.text = resources.getString(R.string.day_and_age_substitution,
                        presenter.processDate(personDetailItem.deathday),
                        presenter.processAge(personDetailItem.birthday, personDetailItem.deathday))
                deathday?.visibility = View.VISIBLE
                deathdayTitle?.visibility = View.VISIBLE
            }
            personDetailItem.birthday.isNotEmpty() -> {
                birthday?.text = resources.getString(R.string.day_and_age_substitution,
                        presenter.processDate(personDetailItem.birthday),
                        presenter.processAge(personDetailItem.birthday))
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
    }

    private fun toggleLoading() {
        progressBar?.visibility =
                if (progressBar?.visibility == View.GONE) View.VISIBLE
                else View.GONE
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

    override fun showDetail(bundle: Bundle, mediaType: String) {
        when (mediaType) {
            SearchPresenter.MEDIA_TYPE_MOVIE ->
                navController?.navigate(R.id.action_personDetailFragment_to_movieDetailFragment, bundle)
            SearchPresenter.MEDIA_TYPE_TV ->
                navController?.navigate(R.id.action_personDetailFragment_to_tvDetailFragment, bundle)
        }
    }

    override fun log(message: String) {
        Timber.d(message)
    }
}