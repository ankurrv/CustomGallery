package com.assessment.comera.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akr.customgallery.callbacks.OnItemClickListener
import com.akr.customgallery.data.model.MediaModel
import com.akr.customgallery.databinding.MediaViewBinding
import com.assessment.comera.ui.viewholders.MediaViewHolder

class GalleryAdapter(
    val context: Activity,
    private val mediaList: List<MediaModel>,
    val listener: OnItemClickListener
) :
    RecyclerView.Adapter<MediaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MediaViewBinding.inflate(inflater, parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(context, mediaList[position])
        holder.itemView.setOnClickListener {
            if (listener != null) {
                listener.onItemClick(mediaList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

}