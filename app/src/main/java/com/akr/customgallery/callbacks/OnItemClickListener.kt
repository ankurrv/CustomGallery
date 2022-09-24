package com.akr.customgallery.callbacks

import com.akr.customgallery.data.model.DirectoryModel

interface OnItemClickListener {
    fun onItemClick(mediaModel: DirectoryModel)
}