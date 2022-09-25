package com.akr.customgallery.ui.viewholders

import android.content.Context
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.akr.customgallery.R
import com.akr.customgallery.data.model.DirectoryModel
import com.akr.customgallery.databinding.DirectoryViewLayoutBinding
import com.akr.customgallery.utilities.Constants
import com.bumptech.glide.Glide
import java.io.File

class DirectoryViewHolder(private val itemBinding: DirectoryViewLayoutBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    // Bind UI with directory data and
    fun bind(context: Context, directoryModel: DirectoryModel, count: Int) {
        itemBinding.directoryNameTV.text = directoryModel.name
        itemBinding.imageCountTV.text = count.toString()
        if (directoryModel.name == Constants.IMAGES)
            itemBinding.directoryNameTV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_image_24,
                0,
                0,
                0
            )
        else if (directoryModel.name == Constants.VIDEOS)
            itemBinding.directoryNameTV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_videocam_24,
                0,
                0,
                0
            )
        else if (directoryModel.name == Constants.CAMERA)
            itemBinding.directoryNameTV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_camera_alt_24,
                0,
                0,
                0
            )
        else
            itemBinding.directoryNameTV.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_folder_open_24,
                0,
                0,
                0
            )


        val photoUri = Uri.fromFile(File(directoryModel.thumbUri))
        // load media thumb with Glide library
        Glide.with(context).load(photoUri).centerCrop().into(itemBinding.directoryThumbIV)
    }
}