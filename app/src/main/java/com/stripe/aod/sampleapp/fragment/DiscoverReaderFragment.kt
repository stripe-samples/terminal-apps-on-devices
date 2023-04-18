package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.adapter.ReaderAdapter
import com.stripe.aod.sampleapp.databinding.FragmentDiscoverReaderBinding
import com.stripe.aod.sampleapp.model.DiscoveryViewModel
import com.stripe.aod.sampleapp.utils.launchAndRepeatWithViewLifecycle

class DiscoverReaderFragment : Fragment(R.layout.fragment_discover_reader) {
    private val discoveryViewModel by viewModels<DiscoveryViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get viewBinding instance
        val viewBinding = FragmentDiscoverReaderBinding.bind(view)
        val readerAdapter = ReaderAdapter(discoveryViewModel)

        viewBinding.swipeRecycler.layoutManager = LinearLayoutManager(activity)
        viewBinding.swipeRecycler.adapter = readerAdapter

        // hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )

        viewBinding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        viewBinding.swipeRefreshLayout.setOnRefreshListener {
            discoveryViewModel.refreshReaderList()
        }

        launchAndRepeatWithViewLifecycle {
            discoveryViewModel.isRefreshing.collect { isNeedRefresh ->
                viewBinding.swipeRefreshLayout.isRefreshing = isNeedRefresh
            }
        }

        launchAndRepeatWithViewLifecycle {
            discoveryViewModel.readers.collect {
                readerAdapter.updateReaders(it)
            }
        }

        launchAndRepeatWithViewLifecycle {
            discoveryViewModel.userMessage.collect { messageResId ->
                messageResId?.let {
                    Snackbar.make(view, messageResId, Snackbar.LENGTH_SHORT).show()
                    discoveryViewModel.clearMessage()
                }
            }
        }

        // start to get Reader list
        discoveryViewModel.refreshReaderList()
    }

    override fun onDestroy() {
        super.onDestroy()
        // release discoveryTask
        discoveryViewModel.stopDiscovery()
    }
}
