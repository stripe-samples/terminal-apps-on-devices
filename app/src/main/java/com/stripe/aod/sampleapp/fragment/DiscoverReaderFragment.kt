package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.adapter.ReaderAdapter
import com.stripe.aod.sampleapp.databinding.FragmentDiscoverReaderBinding
import com.stripe.aod.sampleapp.utils.backToPrevious
import com.stripe.aod.sampleapp.viewmodel.DiscoveryViewModel
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.callable.DiscoveryListener
import com.stripe.stripeterminal.external.models.DiscoveryConfiguration
import com.stripe.stripeterminal.external.models.DiscoveryMethod
import com.stripe.stripeterminal.external.models.Reader
import com.stripe.stripeterminal.external.models.TerminalException
import java.lang.ref.WeakReference

class DiscoverReaderFragment : Fragment(R.layout.fragment_discover_reader), DiscoveryListener{
    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.DiscoverReaderFragment"
    }

    private val discoveryViewModel by viewModels<DiscoveryViewModel>()
    private lateinit var readerAdapter: ReaderAdapter
    private lateinit var config: DiscoveryConfiguration
    private lateinit var discoveryListener: DiscoveryListener
    private var _viewBinding : FragmentDiscoverReaderBinding? = null
    private val viewBinding get() = _viewBinding!!

    private val discoveryCallback: Callback = object : Callback {
        override fun onSuccess() {
            Log.d(Config.TAG, "discoveryCallback onSuccess")
            viewBinding.swipeRefreshLayout.isRefreshing = false
        }

        override fun onFailure(e: TerminalException) {
            Log.d(Config.TAG, "discoveryCallback onFailure")
            viewBinding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentDiscoverReaderBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData();
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun initData() {
        discoveryListener = this;

        val activityRef = WeakReference(activity as MainActivity)
        config = DiscoveryConfiguration(0, DiscoveryMethod.HANDOFF, false, "tml_EuNHgQKLYK66aT")
        viewBinding.swipeRecycler.layoutManager = LinearLayoutManager(activity)
        readerAdapter = ReaderAdapter(activityRef)
        viewBinding.swipeRecycler.adapter = readerAdapter

        viewBinding.swipeRefreshLayout.isRefreshing = true
        if (discoveryViewModel.discoveryTask == null ) {
            if (Terminal.getInstance().connectedReader == null) {
                discoveryViewModel.discoveryTask =
                    Terminal.getInstance().discoverReaders(config, this, discoveryCallback)
            } else {
                Terminal.getInstance().disconnectReader(object: Callback{
                    override fun onFailure(e: TerminalException) {

                    }

                    override fun onSuccess() {
                        discoveryViewModel.discoveryTask =
                            Terminal.getInstance().discoverReaders(config, this@DiscoverReaderFragment, discoveryCallback)
                    }
                })
            }
        }

    }

    private fun initView() {
        //hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(activity as MainActivity, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity?.backToPrevious()
            }
        })

        viewBinding.swipeRefreshLayout.setOnRefreshListener {
            if (discoveryViewModel.discoveryTask == null) {
                discoveryViewModel.discoveryTask = Terminal.getInstance().discoverReaders(config,discoveryListener, discoveryCallback)
            } else {
                discoveryViewModel.discoveryTask?.cancel(object: Callback{
                    override fun onFailure(e: TerminalException) {
                        viewBinding.swipeRefreshLayout.isRefreshing = false
                    }

                    override fun onSuccess() {
                        discoveryViewModel.discoveryTask = Terminal.getInstance().discoverReaders(config,discoveryListener, discoveryCallback)
                    }
                })
            }
        }

        viewBinding.rlBack.setOnClickListener {
            activity?.backToPrevious()
        }
    }

    override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
        Log.d( Config.TAG, "onUpdateDiscoveredReaders readers size = " + readers.size)
        viewBinding.swipeRefreshLayout.isRefreshing = false
        activity?.runOnUiThread {
            discoveryViewModel.readers.value = readers
            readerAdapter.updateReaders(readers)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //release discoveryTask
        discoveryViewModel.discoveryTask?.let { discoveryTask ->
            discoveryTask.cancel(object : Callback {
                override fun onSuccess() {
                    Log.d(Config.TAG, "discoveryTask cancel onSuccess ")
                    discoveryViewModel.discoveryTask = null
                }

                override fun onFailure(e: TerminalException) {
                    Log.d(Config.TAG, "discoveryTask cancel onFailure: " + e.errorMessage)
                    discoveryViewModel.discoveryTask = null
                }
            })
        }
    }
}