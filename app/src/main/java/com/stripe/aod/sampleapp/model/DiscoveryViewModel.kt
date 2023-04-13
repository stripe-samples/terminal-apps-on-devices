package com.stripe.aod.sampleapp.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.adapter.data.ReaderListItem
import com.stripe.aod.sampleapp.utils.toast
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.callable.Cancelable
import com.stripe.stripeterminal.external.callable.DiscoveryListener
import com.stripe.stripeterminal.external.callable.ReaderCallback
import com.stripe.stripeterminal.external.models.ConnectionConfiguration
import com.stripe.stripeterminal.external.models.DiscoveryConfiguration
import com.stripe.stripeterminal.external.models.DiscoveryMethod
import com.stripe.stripeterminal.external.models.Reader
import com.stripe.stripeterminal.external.models.TerminalException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DiscoveryViewModel : ViewModel() {

    private val _readers: MutableStateFlow<List<ReaderListItem>> = MutableStateFlow(emptyList())
    val readers: StateFlow<List<ReaderListItem>> get() = _readers.asStateFlow()

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    private var discoveryTask: Cancelable? = null
    private val config = DiscoveryConfiguration(0, DiscoveryMethod.HANDOFF, false)

    private val discoveryListener: DiscoveryListener = object : DiscoveryListener {
        override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
            updateRefreshStatus(status = false)

            val readerList = readers.map { ReaderListItem(it) }
            _readers.value = readerList
        }
    }

    private val discoveryCallback: Callback = object : Callback {
        override fun onSuccess() {
            Log.d(Config.TAG, "discoveryCallback onSuccess")
        }

        override fun onFailure(e: TerminalException) {
            Log.d(Config.TAG, "discoveryCallback onFailure")
        }
    }

    private val discoveryCancelCallback: Callback = object : Callback {
        override fun onSuccess() {
            Log.d(Config.TAG, "discoveryCancelCallback onSuccess")
        }

        override fun onFailure(e: TerminalException) {
            Log.d(Config.TAG, "discoveryCancelCallback onFailure")
        }
    }

    private fun updateRefreshStatus(status: Boolean) {
        _isRefreshing.value = status
    }

    private fun discoveryReaders(): Cancelable {
        return Terminal.getInstance().discoverReaders(config, discoveryListener, discoveryCallback)
    }

    fun refreshReaderList() {
        updateRefreshStatus(status = true)

        discoveryTask?.cancel(discoveryCancelCallback)

        getCurrentReader()?.let {
            Terminal.getInstance().disconnectReader(object : Callback {
                override fun onFailure(e: TerminalException) {
                    Log.e(Config.TAG, "Disconnect reader fail")
                }

                override fun onSuccess() {
                    Log.e(Config.TAG, "Disconnect reader success")
                }
            })
        }

        discoveryTask = discoveryReaders()
    }

    fun stopDiscovery(onSuccess: () -> Unit = { }) {
        discoveryTask?.cancel(object : Callback {
            override fun onSuccess() {
                discoveryTask = null
                onSuccess()
            }

            override fun onFailure(e: TerminalException) {
                discoveryTask = null
            }
        }) ?: run {
            onSuccess()
        }
    }

    fun connectReader(context: Context, targetReader: Reader) {
        getCurrentReader()?.let { reader ->
            // same one , skip
            if (targetReader.id == reader.id) {
                context.toast(R.string.status_reader_connected)
                return
            }

            // different reader , disconnect old first then connect new one again
            val currentReader: Reader = reader
            Terminal.getInstance().disconnectReader(object : Callback {
                override fun onSuccess() {
                    Log.d(Config.TAG, "Current Reader [ ${currentReader.id} ] disconnect success ")
                    updateReaderList(reader, isConnected = false)
                }

                override fun onFailure(e: TerminalException) {
                    Log.e(Config.TAG, "Current Reader [ ${currentReader.id} ] disconnect fail ")
                }
            })
        }

        if (targetReader.networkStatus != Reader.NetworkStatus.ONLINE) {
            context.toast(R.string.status_reader_offline)
            return
        }

        Log.i(Config.TAG, "Connecting to new Reader [ ${targetReader.id} ] .... ")

        val readerCallback: ReaderCallback = object : ReaderCallback {
            override fun onSuccess(reader: Reader) {
                updateReaderList(reader, isConnected = true)
            }

            override fun onFailure(e: TerminalException) {
            }
        }

        Terminal.getInstance().connectHandoffReader(
            targetReader,
            ConnectionConfiguration.HandoffConnectionConfiguration(),
            null,
            readerCallback,
        )
    }

    fun getCurrentReader(): Reader? {
        return Terminal.getInstance().connectedReader
    }

    private fun updateReaderList(reader: Reader, isConnected: Boolean) {
        _readers.update { readerItem ->
            readerItem.map {
                if (it.reader.id == reader.id) {
                    ReaderListItem(
                        reader,
                        isConnected = isConnected,
                    )
                } else {
                    it
                }
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        stopDiscovery()
    }
}
