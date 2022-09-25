package com.akr.customgallery.ui.viewholders

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.akr.customgallery.data.model.MediaModel
import com.akr.customgallery.databinding.MediaViewBinding
import com.akr.customgallery.utilities.Constants
import com.bumptech.glide.Glide
import java.io.File

class MediaViewHolder(private val itemBinding: MediaViewBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    // bind UI with media data
    fun bind(context: Context, directoryModel: MediaModel) {

        val photoUri = Uri.fromFile(File(directoryModel.thumbUri))
        // load media thumb with Glide library
        Glide.with(context).load(photoUri).centerCrop().into(itemBinding.thumbView)

        if (photoUri.toString().endsWith(Constants.VIDEO_TYPE)) {
            itemBinding.mediaType.visibility = View.VISIBLE // show media is video
        } else
            itemBinding.mediaType.visibility = View.INVISIBLE
    }
}