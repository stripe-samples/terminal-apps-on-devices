package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.adapter.ReaderAdapter
import com.stripe.aod.sampleapp.databinding.FragmentDiscoverReaderBinding
import com.stripe.aod.sampleapp.model.DiscoveryViewModel

class DiscoverReaderFragment : Fragment(R.layout.fragment_discover_reader) {
    private val discoveryViewModel by viewModels<DiscoveryViewModel>()
    private lateinit var readerAdapter: ReaderAdapter
    private lateinit var viewBinding: FragmentDiscoverReaderBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        // get viewBinding instance
        viewBinding = FragmentDiscoverReaderBinding.bind(view)

        viewBinding.swipeRecycler.layoutManager = LinearLayoutManager(activity)
        readerAdapter = ReaderAdapter(discoveryViewModel)
        viewBinding.swipeRecycler.adapter = readerAdapter

        // hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(
            activity as MainActivity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            },
        )

        viewBinding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        viewBinding.swipeRefreshLayout.setOnRefreshListener {
            discoveryViewModel.refreshReaderList()
        }

        lifecycleScope.launchWhenStarted {
            discoveryViewModel.isRefreshing.collect { isNeedRefresh ->
                viewBinding.swipeRefreshLayout.isRefreshing = isNeedRefresh
            }
        }

        lifecycleScope.launchWhenStarted {
            discoveryViewModel.readers.collect {
                readerAdapter.updateReaders(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            discoveryViewModel.isNeedUpdateReaderStatus.collect { isReaderStatusUpdate ->
                if (isReaderStatusUpdate) {
                    readerAdapter.refreshUI()
                    discoveryViewModel.updateReaderStatus(status = false)
                }
            }
        }

        // start to get Reader list base on given locationID
        discoveryViewModel.refreshReaderList()
    }

    override fun onDestroy() {
        super.onDestroy()
        // release discoveryTask
        discoveryViewModel.stopDiscovery()
    }
}
