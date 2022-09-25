package com.akr.customgallery.ui.views

import android.app.Activity
import android.content.DialogInterface.OnShowListener
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.akr.customgallery.R
import com.akr.customgallery.adapters.GalleryAdapter
import com.akr.customgallery.callbacks.OnItemClickListener
import com.akr.customgallery.data.model.MediaModel
import com.akr.customgallery.databinding.BottomSheetDialogBinding
import com.akr.customgallery.databinding.FragmentGalleryDetailsBinding
import com.akr.customgallery.ui.viewmodels.GalleryDetailsViewModel
import com.akr.customgallery.utilities.Constants
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import java.io.File


class GalleryDetailsFragment : Fragment(), OnItemClickListener {

    private lateinit var viewModel: GalleryDetailsViewModel

    private var _binding: FragmentGalleryDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mediaList: Flow<List<MediaModel>>
    private lateinit var mediaListModel: List<MediaModel>
    private lateinit var galleryAdapter: GalleryAdapter

    private var exoPlayer: ExoPlayer? = null
    private var playbackPosition = 0L
    private var playWhenReady = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryDetailsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[GalleryDetailsViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUI()
        setupObservers()

    }

    private fun setUpUI() {
        val directoryName = arguments?.getString("directoryName").toString()
        (activity as AppCompatActivity).supportActionBar?.title = directoryName

        // get media as per selected dirctory
        lifecycleScope.launchWhenCreated {
            mediaList = viewModel.getMediaFromDirectory(directoryName)
        }
    }

    private fun setupObservers() {
        initData()
    }

    private fun initData() {

        lifecycleScope.launchWhenCreated {
            mediaList.collectLatest { it ->
                mediaListModel = it
                mediaListModel.size
                binding.galleryRecyclerView.layoutManager = GridLayoutManager(activity, 4)

                var reversedList = mediaListModel.reversed() // reverse list to show latest at top
                // load media in adapter and show
                galleryAdapter =
                    activity?.let { it1 ->
                        GalleryAdapter(
                            it1,
                            reversedList,
                            this@GalleryDetailsFragment
                        )
                    }!!
                binding.galleryRecyclerView.adapter = galleryAdapter

            }
        }
    }

    // BottomSheetDialog to show selected images and to play selected video
    private fun showBottomSheetDialog(model: MediaModel) {
        val bottomSheetBinding = BottomSheetDialogBinding.inflate(layoutInflater)
        val dialog = activity?.let { BottomSheetDialog(it) }
        dialog?.setContentView(bottomSheetBinding.root)
        val photoUri = Uri.fromFile(File(model.thumbUri))

        dialog?.setOnShowListener {
            OnShowListener { dialogInterface ->
                val bottomSheetDialog = dialogInterface as BottomSheetDialog
                setupFullHeight(bottomSheetDialog)
                bottomSheetDialog.setCancelable(false)
            }
        }
        bottomSheetBinding.fullImageView.minimumHeight = getWindowHeight() - 210
        // load full image using with glide library
        activity?.let {
            Glide.with(it).load(photoUri).into(bottomSheetBinding.fullImageView)
        }
        if (model.thumbUri.toString().endsWith(Constants.VIDEO_TYPE)) {
            preparePlayer(bottomSheetBinding, photoUri)
            bottomSheetBinding.fullImageView.visibility = View.INVISIBLE
            bottomSheetBinding.playerView.visibility = View.VISIBLE
        } else {
            bottomSheetBinding.fullImageView.visibility = View.VISIBLE
            bottomSheetBinding.playerView.visibility = View.INVISIBLE
        }
        dialog?.setOnDismissListener {
            if (model.thumbUri.toString().endsWith(Constants.VIDEO_TYPE)) {
                // Release media player at dialog dismiss
                releasePlayer()
            }
        }

        dialog?.show()
    }

    // open full screen dialog
    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight - 210
        }
        bottomSheet.layoutParams = layoutParams
        behavior.skipCollapsed = false
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onItemClick(mediaModel: Any) {
        val model = mediaModel as MediaModel
        // open selected media file in full screen in dialog format
        // slide down to cancel or dismiss full screen mode of meida.
        showBottomSheetDialog(model)
    }

    // Prepare exoplayer to load selected video files.
    private fun preparePlayer(binding: BottomSheetDialogBinding, uri: Uri) {
        exoPlayer = activity?.let { ExoPlayer.Builder(it).build() }
        exoPlayer?.playWhenReady = true
        binding.playerView.player = exoPlayer

        // open video file from uri to play in exoplayer
        val mediaSources = arrayOfNulls<MediaSource>(1)
        mediaSources[0] = ProgressiveMediaSource.Factory(FileDataSource.Factory())
            .createMediaSource(MediaItem.fromUri(uri))
        mediaSources[0]?.let { exoPlayer?.setMediaSource(it) }

        exoPlayer?.seekTo(playbackPosition)
        exoPlayer?.playWhenReady = playWhenReady
        exoPlayer?.prepare()
    }

    // Release exoplayer after used.
    private fun releasePlayer() {
        exoPlayer?.let { player ->
            playbackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
            exoPlayer = null
        }
    }


}