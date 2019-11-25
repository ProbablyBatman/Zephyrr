package greenberg.moviedbshell.view

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.adapters.ImageGalleryAdapter
import greenberg.moviedbshell.base.BaseDialogFragment
import greenberg.moviedbshell.state.BackdropImageGalleryState
import greenberg.moviedbshell.viewmodel.BackdropImageGalleryViewModel
import timber.log.Timber

class BackdropImageGalleryDialog : BaseDialogFragment() {

    override val mvrxViewId by lazy { backdropViewUUID }
    private lateinit var backdropViewUUID: String

    val backdropImageGalleryViewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.backdropImageGalleryViewModelFactory
    }

    private val viewModel: BackdropImageGalleryViewModel by fragmentViewModel()

    private var progressBar: ProgressBar? = null
    private lateinit var viewPager: ViewPager2
    private var currentImage: ImageView? = null
    private var bottomSheetExpander: ImageView? = null
    private var bottomSheetCopier: TextView? = null
    private var bottomSheetDownload: TextView? = null
    private var coordinatorLayout: CoordinatorLayout? = null
    private lateinit var errorTextView: TextView
    private lateinit var errorRetryButton: MaterialButton
    private lateinit var bottomSheet: LinearLayout
    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var imageGalleryAdapter: ImageGalleryAdapter
    private var entityId = -1

    private var downloadReceiver: BroadcastReceiver? = null
    private var downloadId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            entityId = arguments?.get("EntityID") as? Int ?: -1
        }
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)

        downloadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId == id && id != -1L) {
                    // send notification
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
        constraintLayout = view.findViewById(R.id.image_gallery_constraint)
        errorTextView = view.findViewById(R.id.image_gallery_error)
        errorRetryButton = view.findViewById(R.id.image_gallery_retry_button)

        imageGalleryAdapter = ImageGalleryAdapter()
        viewPager.adapter = imageGalleryAdapter

        bottomSheet = view.findViewById(R.id.image_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        // todo: fix this value, will probably have to differ for tablets
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

        // Investigate if there's a way to page this
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Collapse bottom sheet every time the user changes
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheetCopier?.setOnClickListener {
                    val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
                    val clip = ClipData.newPlainText("zephyrr_image_gallery_link", getCurrentImageLink())
                    clipboard?.primaryClip = clip
                    Toast.makeText(requireContext(), R.string.copy_image_toast, Toast.LENGTH_SHORT).show()
                }
                bottomSheetDownload?.setOnClickListener { startDownload() }
            }
        })

        errorRetryButton.setOnClickListener {
            Timber.d("sag am I even hitting this")
            viewModel.fetchBackdropPosters()
        }

        viewModel.fetchBackdropPosters()
        viewModel.subscribe { Timber.d("State is $it") }
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(downloadReceiver)
        super.onDestroy()
    }

    private fun showLoading() {
        Timber.d("Show Loading")
        hideAllViews()
        hideErrorState()
        showLoadingBar()
    }

    private fun showError(throwable: Throwable) {
        Timber.d("Showing Error")
        Timber.e(throwable)
        hideLoadingBar()
        hideBottomSheet()
        showErrorState()
    }

    private fun showImages(state: BackdropImageGalleryState) {
        Timber.d("Showing image")
        val items = state.backdropItems

        if (items != null) {
            imageGalleryAdapter.backdrops = items
            imageGalleryAdapter.notifyDataSetChanged()
        }
        hideLoadingBar()
        hideErrorState()
        showAllViews()
        showBottomSheet()
    }

    private fun preloadNextImage(right: String) {
        // no-op
    }

    private fun hideAllViews() {
        progressBar?.visibility = View.GONE
        viewPager.visibility = View.GONE
        currentImage?.visibility = View.GONE
        bottomSheetExpander?.visibility = View.GONE
        bottomSheetCopier?.visibility = View.GONE
        errorTextView.visibility = View.GONE
    }

    private fun showAllViews() {
        progressBar?.visibility = View.VISIBLE
        viewPager.visibility = View.VISIBLE
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

    private fun hideErrorState() {
        constraintLayout.visibility = View.GONE
        errorTextView.visibility = View.GONE
        errorRetryButton.visibility = View.GONE
        viewPager.visibility = View.VISIBLE
    }

    private fun showErrorState() {
        constraintLayout.visibility = View.VISIBLE
        errorTextView.visibility = View.VISIBLE
        errorRetryButton.visibility = View.VISIBLE
        viewPager.visibility = View.GONE
    }

    private fun hideBottomSheet() {
        bottomSheet.visibility = View.GONE
    }

    private fun showBottomSheet() {
        bottomSheet.visibility = View.VISIBLE
    }

    // TODO: Architecturally speaking, I'm pretty confident that these aren't supposed to be here,
    // so find out where they are supposed to be.
    private fun getCurrentImageLink(): String {
        return requireContext().getString(R.string.poster_url_substitution, imageGalleryAdapter.backdrops[viewPager.currentItem].filePath)
    }

    private fun startDownload(): Long {
        val link = getCurrentImageLink()
        // This is just the path
        val imageName = imageGalleryAdapter.backdrops[viewPager.currentItem].filePath
        Timber.d("Downloading $link")
        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        val request = DownloadManager.Request(Uri.parse(link))
                .setTitle("Image Saved")
                .setDescription("$imageName saved")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(requireContext().getString(R.string.app_name), imageName)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false)
        return downloadManager?.enqueue(request) ?: -1
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            when (state.backdropItemResponse) {
                Uninitialized -> Timber.d("uninitialized")
                is Loading -> {
                    showLoading()
                }
                is Success -> {
                    showImages(state)
                }
                is Fail -> {
                    showError(state.backdropItemResponse.error)
                }
            }
        }
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    companion object {
        @JvmField
        val TAG: String = BackdropImageGalleryDialog::class.java.simpleName
    }
}
