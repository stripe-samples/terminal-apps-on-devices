package com.stripe.aod.sampleapp.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.listener.TerminalEventListener
import com.stripe.aod.sampleapp.network.TokenProvider
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.callable.Cancelable
import com.stripe.stripeterminal.external.callable.DiscoveryListener
import com.stripe.stripeterminal.external.callable.HandoffReaderListener
import com.stripe.stripeterminal.external.callable.ReaderCallback
import com.stripe.stripeterminal.external.models.ConnectionConfiguration
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.external.models.DisconnectReason
import com.stripe.stripeterminal.external.models.DiscoveryConfiguration
import com.stripe.stripeterminal.external.models.PaymentStatus
import com.stripe.stripeterminal.external.models.Reader
import com.stripe.stripeterminal.external.models.ReaderEvent
import com.stripe.stripeterminal.external.models.TerminalException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val tokenProvider = TokenProvider(viewModelScope)

    private val _readerConnectStatus: MutableStateFlow<ConnectionStatus> = MutableStateFlow(
        ConnectionStatus.NOT_CONNECTED
    )
    val readerConnectStatus: StateFlow<ConnectionStatus> = _readerConnectStatus.asStateFlow()
    private val _readerPaymentStatus: MutableStateFlow<PaymentStatus> = MutableStateFlow(
        PaymentStatus.NOT_READY
    )
    val readerPaymentStatus: StateFlow<PaymentStatus> = _readerPaymentStatus.asStateFlow()

    private var discoveryTask: Cancelable? = null
    private val config = DiscoveryConfiguration.HandoffDiscoveryConfiguration()

    private val _userMessage: MutableStateFlow<String> = MutableStateFlow("")
    val userMessage: StateFlow<String> = _userMessage.asStateFlow()

    private lateinit var targetReader: Reader

    private val discoveryListener: DiscoveryListener = object : DiscoveryListener {
        override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
            val reader = readers.firstOrNull { it.networkStatus == Reader.NetworkStatus.ONLINE }
            if (reader != null) {
                targetReader = reader
                connectReader()
            } else {
                _userMessage.update {
                    "Register a reader using the device settings to start accepting payments"
                }
            }
        }
    }

    private val discoveryCallback: Callback = object : Callback {
        override fun onSuccess() {
            Log.d(Config.TAG, "discoveryCallback onSuccess")
        }

        override fun onFailure(e: TerminalException) {
            Log.e(Config.TAG, "discoveryCallback onFailure", e)
        }
    }

    init {
        viewModelScope.launch {
            launch {
                TerminalEventListener.onConnectionStatusChange.collect(::updateConnectStatus)
            }

            launch {
                TerminalEventListener.onPaymentStatusChange.collect(::updatePaymentStatus)
            }
        }
    }

    @Suppress("MissingPermission")
    fun discoveryReaders() {
        discoveryTask = Terminal.getInstance().discoverReaders(
            config,
            discoveryListener,
            discoveryCallback
        )
    }

    fun connectReader() {
        getCurrentReader()?.let { reader ->
            // same one , skip
            if (targetReader.id == reader.id) {
                return
            }

            // different reader , disconnect old first then connect new one again
            val currentReader: Reader = reader
            Terminal.getInstance().disconnectReader(object : Callback {
                override fun onSuccess() {
                    Log.d(Config.TAG, "Current Reader [ ${currentReader.id} ] disconnect success ")
                }

                override fun onFailure(e: TerminalException) {
                    Log.e(Config.TAG, "Current Reader [ ${currentReader.id} ] disconnect fail ")
                }
            })
        }

        Log.i(Config.TAG, "Connecting to new Reader [ ${targetReader.id} ] .... ")
        val readerCallback: ReaderCallback = object : ReaderCallback {
            override fun onSuccess(reader: Reader) {
                Log.i(Config.TAG, "Reader [ ${targetReader.id} ] Connected ")
            }

            override fun onFailure(e: TerminalException) {
                _userMessage.update { e.errorMessage }
            }
        }

        Terminal.getInstance().connectReader(
            targetReader,
            ConnectionConfiguration.HandoffConnectionConfiguration(
                object : HandoffReaderListener {
                    override fun onDisconnect(reason: DisconnectReason) {
                        Log.i(Config.TAG, "onDisconnect: $reason")
                    }

                    override fun onReportReaderEvent(event: ReaderEvent) {
                        Log.i(Config.TAG, "onReportReaderEvent: $event")
                    }
                }
            ),
            readerCallback
        )
    }

    private fun updateConnectStatus(status: ConnectionStatus) {
        _readerConnectStatus.update { status }
    }

    private fun updatePaymentStatus(status: PaymentStatus) {
        _readerPaymentStatus.update { status }
    }

    private fun getCurrentReader(): Reader? {
        return Terminal.getInstance().connectedReader
    }

    private fun stopDiscovery() {
        discoveryTask?.cancel(object : Callback {
            override fun onSuccess() {
                discoveryTask = null
            }

            override fun onFailure(e: TerminalException) {
                discoveryTask = null
            }
        })

        Terminal.getInstance().disconnectReader(object : Callback {
            override fun onFailure(e: TerminalException) {
            }

            override fun onSuccess() {
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        stopDiscovery()
    }
}
