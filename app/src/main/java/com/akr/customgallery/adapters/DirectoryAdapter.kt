package com.assessment.comera.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akr.customgallery.callbacks.OnItemClickListener
import com.akr.customgallery.data.model.DirectoryModel
import com.akr.customgallery.databinding.DirectoryViewLayoutBinding
import com.assessment.comera.ui.viewholders.DirectoryViewHolder

class DirectoryAdapter(

    val context: Activity,
    private val directoryHashMap: HashMap<String, MutableList<DirectoryModel>>,
    val listener: OnItemClickListener
) :
    RecyclerView.Adapter<DirectoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DirectoryViewLayoutBinding.inflate(inflater, parent, false)
        return DirectoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {

        val key = directoryHashMap.keys.sorted().elementAt(position)
        val list = directoryHashMap.get(key)
        if (list != null) {
            // Collections.sort(list)
            val model = list.get(0)
            holder.bind(context, model, list.size)
            holder.itemView.setOnClickListener {
                if (listener != null) {
                    listener.onItemClick(model)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return directoryHashMap.size
    }
}