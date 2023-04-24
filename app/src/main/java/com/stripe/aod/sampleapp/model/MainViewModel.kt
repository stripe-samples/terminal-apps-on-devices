package com.stripe.aod.sampleapp.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.network.TokenProvider
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

class MainViewModel : ViewModel() {
    val tokenProvider = TokenProvider(viewModelScope)

    private val _isReaderConnected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isReaderConnected: StateFlow<Boolean> = _isReaderConnected.asStateFlow()

    private var discoveryTask: Cancelable? = null
    private val config = DiscoveryConfiguration(0, DiscoveryMethod.HANDOFF, false)

    private val _userMessage: MutableStateFlow<String> = MutableStateFlow("")
    val userMessage: StateFlow<String> = _userMessage.asStateFlow()

    private lateinit var targetReader: Reader

    private val discoveryListener: DiscoveryListener = object : DiscoveryListener {
        override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
            if (readers.isNotEmpty()) {
                targetReader = readers[0]
                connectReader()
            }
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
                    _isReaderConnected.update { false }
                }

                override fun onFailure(e: TerminalException) {
                    Log.e(Config.TAG, "Current Reader [ ${currentReader.id} ] disconnect fail ")
                }
            })
        }

        Log.i(Config.TAG, "Connecting to new Reader [ ${targetReader.id} ] .... ")
        val readerCallback: ReaderCallback = object : ReaderCallback {
            override fun onSuccess(reader: Reader) {
                _isReaderConnected.update { true }
            }

            override fun onFailure(e: TerminalException) {
                _userMessage.update { e.errorMessage }
            }
        }

        Terminal.getInstance().connectHandoffReader(
            targetReader,
            ConnectionConfiguration.HandoffConnectionConfiguration(),
            null,
            readerCallback
        )
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
