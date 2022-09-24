package com.akr.customgallery.ui.views

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.akr.customgallery.R
import com.akr.customgallery.ui.viewmodels.GalleryDetailsViewModel

class GalleryDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = GalleryDetailsFragment()
    }

    private lateinit var viewModel: GalleryDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GalleryDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}