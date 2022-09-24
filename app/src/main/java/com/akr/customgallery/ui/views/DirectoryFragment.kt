package com.akr.customgallery.ui.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.akr.customgallery.R
import com.akr.customgallery.databinding.FragmentDirectoryBinding
import com.akr.customgallery.ui.viewmodels.DirectoryViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DirectoryFragment : Fragment() {

    private val PICK_FROM_GALLERY = 1

    private var _binding: FragmentDirectoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var directoryViewModel: DirectoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDirectoryBinding.inflate(inflater, container, false)
        directoryViewModel = ViewModelProvider(this)[DirectoryViewModel::class.java]
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpUI();
    }

    private fun setUpUI(){
        binding.buttonOpenGallery.setOnClickListener {

            if (activity?.let { it1 ->
                    ActivityCompat.checkSelfPermission(
                        it1,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                activity?.let { it1 ->
                    ActivityCompat.requestPermissions(
                        it1,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ),
                        PICK_FROM_GALLERY
                    )
                }
            } else {

                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}