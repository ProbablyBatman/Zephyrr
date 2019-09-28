package greenberg.moviedbshell.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.adapters.ImageGalleryAdapter
import greenberg.moviedbshell.base.BaseFragment
import greenberg.moviedbshell.models.imagegallerymodels.BackdropsItem
import greenberg.moviedbshell.presenters.BackdropImageGalleryPresenter
import timber.log.Timber

class BackdropImageGalleryFragment :
        BaseFragment<BackdropImageGalleryView, BackdropImageGalleryPresenter>(),
        BackdropImageGalleryView {

    private var progressBar: ProgressBar? = null
    private var viewPager: ViewPager2? = null
    private var currentImage: ImageView? = null
    private var bottomSheetExpander: ImageView? = null
    private var bottomSheetCopier: TextView? = null

    private var imageGalleryAdapter: ImageGalleryAdapter? = null
    private var entityId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            entityId = arguments?.get("EntityID") as? Int ?: -1
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.backdrop_image_gallery_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.image_gallery_progress_bar)
        viewPager = view.findViewById(R.id.image_gallery_view_pager)
        currentImage = view.findViewById(R.id.image_gallery_current)
        bottomSheetExpander = view.findViewById(R.id.image_bottom_sheet_expand)
        bottomSheetCopier = view.findViewById(R.id.image_bottom_sheet_copy)

        val bottomSheet = view.findViewById<LinearLayout>(R.id.image_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        //todo: fix this value
        bottomSheetBehavior.peekHeight = 112
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Timber.d("sag onslide")
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                Timber.d("sag newstate is $newState")
            }
        })

        presenter.initView()
        presenter.loadImages(entityId)
    }

    override fun createPresenter(): BackdropImageGalleryPresenter = presenter
            ?: (activity?.application as ZephyrrApplication).component.backdropImageGalleryPresenter()

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun showLoading() {
        Timber.d("Show Loading")
        hideAllViews()
//        hideErrorState()
        showLoadingBar()
    }

    override fun showError(throwable: Throwable) {
        Timber.d("Showing Error")
        Timber.e(throwable)
        hideLoadingBar()
//        showErrorState()
    }

    override fun showImages(items: List<BackdropsItem>) {
        Timber.d("Showing image")

        imageGalleryAdapter = ImageGalleryAdapter(items)
        viewPager?.adapter = imageGalleryAdapter
        hideLoadingBar()
        showAllViews()
    }

    override fun preloadNextImage(right: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun hideAllViews() {
        progressBar?.visibility = View.GONE
        viewPager?.visibility = View.GONE
        currentImage?.visibility = View.GONE
        bottomSheetExpander?.visibility = View.GONE
        bottomSheetCopier?.visibility = View.GONE
    }

    private fun showAllViews() {
        progressBar?.visibility = View.VISIBLE
        viewPager?.visibility = View.VISIBLE
        currentImage?.visibility = View.VISIBLE
        bottomSheetExpander?.visibility = View.VISIBLE
        bottomSheetCopier?.visibility = View.VISIBLE
    }

    private fun showLoadingBar() {
        progressBar?.visibility = View.VISIBLE
    }

    private fun hideLoadingBar() {
        progressBar?.visibility = View.GONE
    }

    //TODO: implement error stuff for this
//    private fun hideErrorState() {
//        errorTextView?.visibility = View.GONE
//        errorRetryButton?.visibility = View.GONE
//    }
//
//    private fun showErrorState() {
//        errorTextView?.visibility = View.VISIBLE
//        errorRetryButton?.visibility = View.VISIBLE
//        scrollView?.visibility = View.VISIBLE
//    }

    override fun log(message: String) {
        Timber.d(message)
    }
}