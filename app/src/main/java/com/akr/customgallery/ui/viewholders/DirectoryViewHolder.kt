package com.assessment.comera.ui.viewholders

import android.content.Context
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.akr.customgallery.R
import com.akr.customgallery.data.model.DirectoryModel
import com.akr.customgallery.databinding.DirectoryViewLayoutBinding
import com.bumptech.glide.Glide
import java.io.File

class DirectoryViewHolder(private val itemBinding: DirectoryViewLayoutBinding) :
    RecyclerView.ViewHolder(itemBinding.root) {
    fun bind(context: Context, directoryModel: DirectoryModel) {
        itemBinding.directoryNameTV.text = directoryModel.name
        itemBinding.imageCountTV.text = directoryModel.totalFiles.toString()
        if(directoryModel.name.equals("All"))
            itemBinding.directoryNameTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_image_24,0,0,0)
        else if(directoryModel.name.equals("Camera"))
            itemBinding.directoryNameTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_camera_alt_24,0,0,0)
        else
            itemBinding.directoryNameTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_folder_open_24,0,0,0)


        val photoUri = Uri.fromFile(File(directoryModel.thumbUri))
        Glide.with(context).load(photoUri).centerCrop().into(itemBinding.directoryThumbIV);
    }
}