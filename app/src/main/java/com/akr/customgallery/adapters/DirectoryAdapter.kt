package com.akr.customgallery.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akr.customgallery.callbacks.OnItemClickListener
import com.akr.customgallery.data.model.DirectoryModel
import com.akr.customgallery.databinding.DirectoryViewLayoutBinding
import com.akr.customgallery.ui.viewholders.DirectoryViewHolder

class DirectoryAdapter(
    val context: Activity,
    private val directoryHashMap: HashMap<String, MutableList<DirectoryModel>>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<DirectoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DirectoryViewLayoutBinding.inflate(inflater, parent, false)
        return DirectoryViewHolder(binding) // Bind Directory view to load in recycler view adapter
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {


        val directoryName = directoryHashMap.keys.sorted()
            .elementAt(position)  // get directory name from current position
        val list =
            directoryHashMap.get(directoryName) // get list of media as per current directory name
        if (list != null) {
            val directoryModel =
                list[0] // get directory model to show last image as directory cover page
            holder.bind(
                context,
                directoryModel,
                list.size
            )  // bind data with view as per current position

            holder.itemView.setOnClickListener {
                listener.onItemClick(directoryModel) // perform callback as per click
            }
        }

    }

    override fun getItemCount(): Int {
        return directoryHashMap.size // return total number of directory
    }
}