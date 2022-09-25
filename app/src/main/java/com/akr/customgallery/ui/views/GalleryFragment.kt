package com.akr.customgallery.ui.views

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
class GalleryFragment : Fragment(), OnItemClickListener, MenuProvider {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var directoryViewModel: DirectoryViewModel

    private lateinit var directoryList: Flow<HashMap<String, MutableList<DirectoryModel>>>
    private lateinit var directoryListModel: HashMap<String, MutableList<DirectoryModel>>
    private lateinit var directoryAdapter: DirectoryAdapter
    private var isGrid: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        directoryViewModel = ViewModelProvider(this)[DirectoryViewModel::class.java]

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUI()
        setupObservers()
    }

    private fun setUpUI() {
        lifecycleScope.launchWhenCreated {
            directoryList = directoryViewModel.getMediaDirectory()!!
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

    override fun onItemClick(mediaModel: Any) {
        val model = mediaModel as DirectoryModel
        val bundle = bundleOf("directoryName" to model.name)
        findNavController().navigate(R.id.action_GalleryFragment_to_GalleryDetailsFragment, bundle)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
        for (i in 0 until menu.size()) {
            val drawable = menu.getItem(i).icon
            if (drawable != null) {
                drawable.mutate()
                drawable.setColorFilter(
                    androidx.appcompat.R.attr.titleTextColor,
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_settings) {
            if (!isGrid) {
                isGrid = true
                menuItem.setIcon(R.drawable.ic_baseline_grid_view_24)
            } else {
                isGrid = false
                menuItem.setIcon(R.drawable.ic_baseline_list_24)
            }
            val drawable = menuItem.icon
            if (drawable != null) {
                drawable.mutate()
                drawable.setColorFilter(
                    androidx.appcompat.R.attr.titleTextColor,
                    PorterDuff.Mode.SRC_ATOP
                )
            }

            binding.directoryRecyclerView.layoutManager = if (isGrid) LinearLayoutManager(activity) else GridLayoutManager(
                activity,
                2
            )
            directoryAdapter.notifyDataSetChanged()

        }
        if (menuItem.itemId == androidx.appcompat.R.id.home) {
            findNavController().navigate(R.id.action_GalleryFragment_to_DirectoryFragment)
        }
        return true
    }
}