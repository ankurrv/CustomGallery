package com.assessment.comera.ui.viewholders

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.akr.customgallery.data.model.MediaModel
import com.akr.customgallery.databinding.MediaViewBinding
import com.bumptech.glide.Glide
import java.io.File

class MediaViewHolder(private val itemBinding: MediaViewBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(context: Context, directoryModel: MediaModel) {

        val photoUri = Uri.fromFile(File(directoryModel.thumbUri))
        Glide.with(context).load(photoUri).centerCrop().into(itemBinding.thumbView)
        if (photoUri.toString().endsWith(".mp4")) {
            itemBinding.mediaType.visibility = View.VISIBLE
        } else
            itemBinding.mediaType.visibility = View.INVISIBLE
    }
}