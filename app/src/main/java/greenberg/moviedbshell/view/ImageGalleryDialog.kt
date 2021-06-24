package greenberg.moviedbshell.view

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import greenberg.moviedbshell.R
import greenberg.moviedbshell.ZephyrrApplication
import greenberg.moviedbshell.adapters.ImageGalleryAdapter
import greenberg.moviedbshell.base.BaseDialogFragment
import greenberg.moviedbshell.state.ImageGalleryState
import greenberg.moviedbshell.viewmodel.ImageGalleryViewModel
import timber.log.Timber

class ImageGalleryDialog : BaseDialogFragment() {

    override val mvrxViewId by lazy { backdropViewUUID }
    private lateinit var backdropViewUUID: String

    val imageGalleryViewModelFactory by lazy {
        (activity?.application as ZephyrrApplication).component.imageGalleryViewModelFactory
    }

    private val viewModel: ImageGalleryViewModel by fragmentViewModel()

    private var progressBar: ProgressBar? = null
    private lateinit var viewPager: ViewPager2
    private var currentImage: ImageView? = null
    private lateinit var bottomSheetExpander: ImageView
    private var bottomSheetCopier: TextView? = null
    private var bottomSheetDownload: TextView? = null
    private var coordinatorLayout: CoordinatorLayout? = null
    private lateinit var errorTextView: TextView
    private lateinit var errorRetryButton: Button
    private lateinit var bottomSheet: FrameLayout
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var bottomSheetContainer: LinearLayout

    private lateinit var imageGalleryAdapter: ImageGalleryAdapter

    private var downloadReceiver: BroadcastReceiver? = null
    private var downloadId = -1L
    private var isBackdrop = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
        isBackdrop = arguments?.getBoolean(BACKDROP_KEY) ?: false

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
        return inflater.inflate(R.layout.image_gallery_layout, container, false)
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
        bottomSheetContainer = view.findViewById(R.id.bottom_sheet_container)

        imageGalleryAdapter = ImageGalleryAdapter()
        viewPager.adapter = imageGalleryAdapter

        bottomSheet = view.findViewById(R.id.image_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        // TODO: this is bizarre but somehow correct
        bottomSheetContainer.viewTreeObserver.addOnGlobalLayoutListener {
            bottomSheetBehavior.peekHeight = bottomSheetExpander.bottom
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // no-op
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED ->
                        bottomSheetExpander.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_menu))
                    BottomSheetBehavior.STATE_EXPANDED ->
                        bottomSheetExpander.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_close_menu))
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
                    clipboard?.setPrimaryClip(clip)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    Toast.makeText(requireContext(), R.string.copy_image_toast, Toast.LENGTH_SHORT).show()
                }
                bottomSheetDownload?.setOnClickListener {
                    // TODO: revisit if this should be here because it feels like it shouldn't
                    if (ContextCompat.checkSelfPermission(
                            this@ImageGalleryDialog.requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        startDownload()
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            when {
                                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                                    AlertDialog.Builder(this@ImageGalleryDialog.requireContext())
                                        .setTitle(R.string.permission_request_title)
                                        .setMessage(R.string.permission_request_description)
                                        .setPositiveButton(R.string.dismiss) { dialog, _ -> dialog.dismiss() }
                                        .create()
                                        .show()
                                }
                                else -> {
                                    // You can directly ask for the permission.
                                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
                                }
                            }
                        }
                    }
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        })

        errorRetryButton.setOnClickListener {
            viewModel.fetchPosters()
        }
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(downloadReceiver)
        super.onDestroy()
    }

    private fun showLoading() {
        log("Show Loading")
        hideAllViews()
        hideErrorState()
        showLoadingBar()
    }

    private fun showError(throwable: Throwable) {
        log("Showing Error")
        log(throwable)
        hideLoadingBar()
        hideBottomSheet()
        showErrorState()
    }

    private fun showImages(state: ImageGalleryState) {
        log("Showing image")
        val items = if (isBackdrop) {
            state.backdropItems
        } else {
            state.posterItems
        }
        imageGalleryAdapter.posters = items
        imageGalleryAdapter.notifyDataSetChanged()
    }

    private fun preloadNextImage(right: String) {
        // no-op
    }

    private fun hideAllViews() {
        progressBar?.visibility = View.GONE
        viewPager.visibility = View.GONE
        currentImage?.visibility = View.GONE
        bottomSheetExpander.visibility = View.GONE
        bottomSheetCopier?.visibility = View.GONE
        errorTextView.visibility = View.GONE
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

    private fun getCurrentImageLink() =
        requireContext().getString(R.string.poster_url_substitution, imageGalleryAdapter.posters[viewPager.currentItem].filePath)

    private fun startDownload(): Long {
        val link = getCurrentImageLink()
        // This is just the path
        val imageName = imageGalleryAdapter.posters[viewPager.currentItem].filePath
        log("Downloading $link")
        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        val request = DownloadManager.Request(Uri.parse(link))
            .setTitle("${getString(R.string.app_name)} poster download")
            .setDescription("saving $imageName")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(requireContext().getString(R.string.app_name), imageName)
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
        return downloadManager?.enqueue(request) ?: -1
    }

    override fun invalidate() {
        withState(viewModel) { state ->
            when (state.imageGalleryResponse) {
                Uninitialized -> log("uninitialized")
                is Loading -> {
                    showLoading()
                }
                is Success -> {
                    hideLoadingBar()
                    hideErrorState()
                    showBottomSheet()
                    showImages(state)
                }
                is Fail -> {
                    showError(state.imageGalleryResponse.error)
                }
            }
        }
    }

    override fun log(message: String) {
        Timber.d(message)
    }

    override fun log(exception: Throwable) {
        Timber.e(exception)
    }

    companion object {
        @JvmField
        val TAG: String = ImageGalleryDialog::class.java.simpleName
        const val BACKDROP_KEY = "BACKDROP"
    }
}
