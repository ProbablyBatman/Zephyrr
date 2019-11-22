package greenberg.moviedbshell.view

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.adapters.ImageGalleryAdapter
import greenberg.moviedbshell.base.BaseDialogFragment
import greenberg.moviedbshell.models.imagegallerymodels.BackdropsItem
import greenberg.moviedbshell.presenters.BackdropImageGalleryPresenter
import timber.log.Timber

class BackdropImageGalleryDialog :
        BaseDialogFragment<BackdropImageGalleryView, BackdropImageGalleryPresenter>(),
        BackdropImageGalleryView {

    private var progressBar: ProgressBar? = null
    private var viewPager: ViewPager2? = null
    private var currentImage: ImageView? = null
    private var bottomSheetExpander: ImageView? = null
    private var bottomSheetCopier: TextView? = null
    private var bottomSheetDownload: TextView? = null
    private var coordinatorLayout: CoordinatorLayout? = null

    private var imageGalleryAdapter: ImageGalleryAdapter? = null
    private var entityId = -1

    private var downloadReceiver: BroadcastReceiver? = null
    private var downloadId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            entityId = arguments?.get("EntityID") as? Int ?: -1
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)

        downloadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId == id && id != -1L) {
                    //send notification
                    Toast.makeText(requireContext(), R.string.image_downloaded, Toast.LENGTH_SHORT).show()
                }
            }
        }
        requireActivity().registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
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
        bottomSheetDownload = view.findViewById(R.id.image_bottom_sheet_download)
        coordinatorLayout = view.findViewById(R.id.image_gallery_coordinator)

        val bottomSheet = view.findViewById<LinearLayout>(R.id.image_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        //todo: fix this value, will probably have to differ for tablets
        bottomSheetBehavior.peekHeight = 200
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // no-op
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED ->
                        bottomSheetExpander?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_menu))
                    BottomSheetBehavior.STATE_EXPANDED ->
                        bottomSheetExpander?.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_close_menu))
                    else -> return
                }
            }
        })

        bottomSheet.setOnClickListener {
            when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                BottomSheetBehavior.STATE_COLLAPSED -> bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        //Investigate if there's a way to page this
        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Collapse bottom sheet every time the user changes
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheetCopier?.setOnClickListener {
                    val currentPosition = viewPager?.currentItem
                    if (currentPosition != null) {
                        val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
                        val clip = ClipData.newPlainText("zephyrr_image_gallery_link", presenter.getCurrentImageLink(currentPosition))
                        clipboard?.primaryClip = clip
                        presenter.startDownload(currentPosition)
                        Toast.makeText(requireContext(), R.string.copy_image_toast, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), R.string.copy_image_error_toast, Toast.LENGTH_SHORT).show()
                    }
                }
                bottomSheetDownload?.setOnClickListener {
                    val currentPosition = viewPager?.currentItem
                    if (currentPosition != null) {
                        presenter.startDownload(currentPosition)
                    }
                }
            }
        })

        presenter.initView()
        presenter.loadImages(entityId)
    }

    override fun createPresenter(): BackdropImageGalleryPresenter = presenter
            ?: (activity?.application as ZephyrrApplication).component.backdropImageGalleryPresenter()

    override fun onResume() {
        super.onResume()
        //(activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        //(activity as AppCompatActivity).supportActionBar?.show()
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(downloadReceiver)
        super.onDestroy()
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

    companion object {
        @JvmField
        val TAG: String = BackdropImageGalleryDialog::class.java.simpleName
    }
}