package com.stripe.aod.sampleapp.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.R
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

class DiscoveryViewModel : ViewModel() {
    private val _readers: MutableStateFlow<List<Reader>> = MutableStateFlow(emptyList())
    val readers: StateFlow<List<Reader>> get() = _readers

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing

    // keep a state to track Reader status(Connected/Disconnected)
    private val _isNeedUpdateReaderStatus: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isNeedUpdateReaderStatus: StateFlow<Boolean> get() = _isNeedUpdateReaderStatus

    private var discoveryTask: Cancelable? = null
    private val config = DiscoveryConfiguration(0, DiscoveryMethod.HANDOFF, false, "tml_EuNHgQKLYK66aT") // just hardcode a locationId here for test

    private val discoveryListener: DiscoveryListener = object : DiscoveryListener {
        override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
            updateRefreshStatus(status = false)
            _readers.value = readers
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

    fun updateRefreshStatus(status: Boolean) {
        _isRefreshing.value = status
    }

    fun updateReaderStatus(status: Boolean) {
        _isNeedUpdateReaderStatus.value = status
    }

    private fun discoveryReaders(): Cancelable {
        return Terminal.getInstance().discoverReaders(config, discoveryListener, discoveryCallback)
    }

    fun refreshReaderList() {
        updateRefreshStatus(status = true)

        discoveryTask?.cancel(discoveryCancelCallback)

        if (Terminal.getInstance().connectedReader != null) {
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

    fun connectReader(context: Context, reader: Reader) {
        val connectedReader = Terminal.getInstance().connectedReader
        if (connectedReader != null) {
            // same one , skip
            if (reader.id === connectedReader.id) {
                context.toast(R.string.status_reader_connected)
                return
            }

            // different reader , disconnect old first then connect new one again
            val lastReader: Reader = connectedReader
            Terminal.getInstance().disconnectReader(object : Callback {
                override fun onSuccess() {
                    Log.d(Config.TAG, "Last Reader [ ${lastReader.id} ] disconnect success ")
                    updateReaderStatus(status = true)
                }

                override fun onFailure(e: TerminalException) {
                    Log.e(Config.TAG, "Last Reader [ ${lastReader.id} ] disconnect fail ")
                }
            })
        }

        if (reader.networkStatus != Reader.NetworkStatus.ONLINE) {
            context.toast(R.string.status_reader_offline)
            return
        }

        Log.i(Config.TAG, "Connecting to new Reader [ ${reader.id} ] .... ")

        val readerCallback: ReaderCallback = object : ReaderCallback {
            override fun onSuccess(reader: Reader) {
                updateReaderStatus(status = true)
            }

            override fun onFailure(e: TerminalException) {
            }
        }

        Terminal.getInstance().connectHandoffReader(
            reader,
            ConnectionConfiguration.HandoffConnectionConfiguration(),
            null,
            readerCallback,
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopDiscovery()
    }
}
