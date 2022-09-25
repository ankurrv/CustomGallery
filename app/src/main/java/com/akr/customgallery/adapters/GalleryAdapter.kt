package com.akr.customgallery.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akr.customgallery.callbacks.OnItemClickListener
import com.akr.customgallery.data.model.MediaModel
import com.akr.customgallery.databinding.MediaViewBinding
import com.akr.customgallery.ui.viewholders.MediaViewHolder

class GalleryAdapter(
    val context: Activity,
    private val mediaList: List<MediaModel>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<MediaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MediaViewBinding.inflate(inflater, parent, false)
        return MediaViewHolder(binding) // bind layout with viewholder
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(context, mediaList[position]) // BInd UI with media data
        holder.itemView.setOnClickListener {
            listener.onItemClick(mediaList[position]) // perform call back operation
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size // Return total number of available media
    }

}