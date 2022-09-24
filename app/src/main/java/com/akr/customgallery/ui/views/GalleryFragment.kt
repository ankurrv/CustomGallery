package com.akr.customgallery.ui.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.akr.customgallery.R
import com.akr.customgallery.callbacks.OnItemClickListener
import com.akr.customgallery.data.model.DirectoryModel
import com.akr.customgallery.databinding.FragmentGalleryBinding
import com.akr.customgallery.ui.viewmodels.DirectoryViewModel
import com.assessment.comera.adapters.DirectoryAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class GalleryFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var directoryViewModel: DirectoryViewModel

    private lateinit var directoryList: Flow<List<DirectoryModel>>
    private lateinit var directoryListModel: List<DirectoryModel>
    private lateinit var directoryAdapter: DirectoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        directoryViewModel = ViewModelProvider(this)[DirectoryViewModel::class.java]
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.buttonSecond.setOnClickListener {
//            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
//        }

        setUpUI();
        setupObservers()
    }

    private fun setUpUI() {

        lifecycleScope.launchWhenCreated {
            directoryList = directoryViewModel?.getMediaDirectory()!!
        }
    }

    private fun setupObservers() {
        initData()
    }

    private fun initData() {

        lifecycleScope.launchWhenCreated {
            directoryList.collectLatest { it ->
                directoryListModel = it
                directoryListModel.size
                binding.directoryRecyclerView.layoutManager = GridLayoutManager(activity, 2)
                directoryAdapter =
                    activity?.let { it1 ->
                        DirectoryAdapter(
                            it1,
                            directoryListModel,
                            this@GalleryFragment
                        )
                    }!!
                binding.directoryRecyclerView.adapter = directoryAdapter

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(mediaModel: DirectoryModel) {
        val bundle = bundleOf("directoryName" to mediaModel.name)
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
    }
}