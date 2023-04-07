package com.stripe.aod.sampleapp.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.Job

class DiscoveryViewModel : ViewModel() {
    val readers: MutableLiveData<List<Reader>> = MutableLiveData(emptyList())
    var discoveryTask: Cancelable? = null
    val isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    var readerUpdateCallBack: ((List<Reader>) -> Unit)? = null
    private val config = DiscoveryConfiguration(0, DiscoveryMethod.HANDOFF, false, "tml_EuNHgQKLYK66aT") // just hardcode a locationId here for test

    private val discoveryListener: DiscoveryListener = object : DiscoveryListener {
        override fun onUpdateDiscoveredReaders(readers: List<Reader>) {
            readerUpdateCallBack?.invoke(readers)
        }
    }

    private val discoveryCallback: Callback = object : Callback {
        override fun onSuccess() {
            Log.d(Config.TAG, "discoveryCallback onSuccess")
            updateRefreshStatus(status = false)
        }

        override fun onFailure(e: TerminalException) {
            Log.d(Config.TAG, "discoveryCallback onFailure")
            updateRefreshStatus(status = false)
        }
    }

    private val discoveryCancelCallback: Callback = object : Callback {
        override fun onSuccess() {
            Log.d(Config.TAG, "discoveryCancelCallback onSuccess")
            discoveryTask = discoveryReaders()
        }

        override fun onFailure(e: TerminalException) {
            Log.d(Config.TAG, "discoveryCancelCallback onFailure")
            updateRefreshStatus(status = false)
        }
    }

    fun updateRefreshStatus(status: Boolean) {
        isRefreshing.postValue(status)
    }

    fun startDiscovery() {
        if (discoveryTask == null) {
            if (Terminal.getInstance().connectedReader == null) {
                discoveryTask = discoveryReaders()
            } else {
                Terminal.getInstance().disconnectReader(object : Callback {
                    override fun onFailure(e: TerminalException) {
                        Log.e(Config.TAG, "onFailure: Disconnect reader fail")
                    }

                    override fun onSuccess() {
                        discoveryTask = discoveryReaders()
                    }
                })
            }
        }
    }

    fun discoveryReaders(): Cancelable {
        return Terminal.getInstance().discoverReaders(config, discoveryListener, discoveryCallback)
    }

    fun refreshReaderList() {
        if (discoveryTask == null) {
            discoveryTask = discoveryReaders()
        } else {
            discoveryTask?.cancel(discoveryCancelCallback)
        }
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

    fun connectReader(context: Context, reader: Reader, readerStatusChangeCallback: (Reader) -> Job) {
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
                    readerStatusChangeCallback(lastReader)
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
                readerStatusChangeCallback(reader)
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
