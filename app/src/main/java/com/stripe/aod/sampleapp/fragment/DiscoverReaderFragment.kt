package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.adapter.ReaderAdapter
import com.stripe.aod.sampleapp.listener.ReaderClickListener
import com.stripe.aod.sampleapp.listener.ReaderConnectStatusListener
import com.stripe.aod.sampleapp.utils.backToPrevious
import com.stripe.aod.sampleapp.viewmodel.DiscoveryViewModel
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.callable.DiscoveryListener
import com.stripe.stripeterminal.external.models.DiscoveryConfiguration
import com.stripe.stripeterminal.external.models.DiscoveryMethod
import com.stripe.stripeterminal.external.models.Reader
import com.stripe.stripeterminal.external.models.TerminalException
import kotlinx.android.synthetic.main.fragment_discover_reader.*
import java.lang.ref.WeakReference

class DiscoverReaderFragment: Fragment(R.layout.fragment_discover_reader), DiscoveryListener, ReaderConnectStatusListener {

    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.DiscoverReaderFragment"
    }

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var discoveryViewModel:DiscoveryViewModel
    private lateinit var readerAdapter: ReaderAdapter
    private lateinit var config: DiscoveryConfiguration
    private lateinit var discoveryListener: DiscoveryListener

    private val discoveryCallback: Callback = object : Callback {
        override fun onSuccess() {
            Log.d(Config.TAG, "discoveryCallback onSuccess")
            swipeRefreshLayout.isRefreshing = false
        }

        override fun onFailure(e: TerminalException) {
            Log.d(Config.TAG, "discoveryCallback onFailure")
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initData();
    }

    private fun initData() {
        discoveryListener = this;
        val discoveryViewModelFactory: DiscoveryViewModelFactory = DiscoveryViewModelFactory()
        discoveryViewModel = ViewModelProvider(this, discoveryViewModelFactory).get(DiscoveryViewModel::class.java)

        val activityRef = WeakReference(activity as MainActivity)
        discoveryViewModel.readerConnectStatusListener = this
        discoveryViewModel.readerClickListener = ReaderClickListener(activityRef, discoveryViewModel)

        config = DiscoveryConfiguration(0, DiscoveryMethod.INTERNET, false, "tml_EuNHgQKLYK66aT")
        if (discoveryViewModel.discoveryTask == null && Terminal.getInstance().connectedReader == null) {
            discoveryViewModel.discoveryTask = Terminal.getInstance().discoverReaders(config, this, discoveryCallback)
        }

        discoveryViewModel.isConnecting.observe(activity as MainActivity) { aBoolean ->
            if (aBoolean) {

            } else {

            }
        }
        discoveryViewModel.isConnecting.value = false

        recyclerView.layoutManager = LinearLayoutManager(activity)
        readerAdapter = ReaderAdapter(activityRef,ArrayList<Reader>(),discoveryViewModel)
        recyclerView.adapter = readerAdapter

        swipeRefreshLayout.isRefreshing = true
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.swipe_recycler)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)

        //hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(activity as MainActivity,object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity?.backToPrevious()
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            if (discoveryViewModel.discoveryTask == null) {
                discoveryViewModel.discoveryTask = Terminal.getInstance().discoverReaders(config,discoveryListener, discoveryCallback)
            } else {
                discoveryViewModel.discoveryTask?.cancel(object: Callback{
                    override fun onFailure(e: TerminalException) {
                        swipeRefreshLayout.isRefreshing = false
                    }

                    override fun onSuccess() {
                        discoveryViewModel.discoveryTask = Terminal.getInstance().discoverReaders(config,discoveryListener, discoveryCallback)
                    }
                })
            }
        }

        rl_back.setOnClickListener {
            activity?.backToPrevious()
        }
    }

    override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
        Log.d( Config.TAG, "onUpdateDiscoveredReaders readers size = " + (readers?.size?: 0))
        swipeRefreshLayout.isRefreshing = false
        activity?.runOnUiThread(Runnable {
            discoveryViewModel.readers.value = readers
            readerAdapter.updateReaders(readers)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        //release discoveryTask
        swipeRefreshLayout.isRefreshing = false;

        if (discoveryViewModel.discoveryTask != null) {
            discoveryViewModel.discoveryTask!!.cancel(object : Callback {
                override fun onSuccess() {
                    Log.d(Config.TAG, "discoveryTask cancel onSuccess ")
                    discoveryViewModel.discoveryTask = null
                }

                override fun onFailure(e: TerminalException) {
                    Log.d(Config.TAG, "discoveryTask cancel onFailure: " + e.errorMessage)
                    swipeRefreshLayout.isRefreshing = false;
                    discoveryViewModel.discoveryTask = null
                }
            })
        }
    }

    internal class DiscoveryViewModelFactory() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DiscoveryViewModel(DiscoveryMethod.INTERNET) as T
        }
    }

    override fun onDisconnectReader() {
        readerAdapter.onReaderStatusChange()
    }

    override fun onConnectReader() {
        readerAdapter.onReaderStatusChange()
    }
}