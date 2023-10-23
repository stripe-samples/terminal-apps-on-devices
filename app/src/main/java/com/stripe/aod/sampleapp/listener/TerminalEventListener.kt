package com.stripe.aod.sampleapp.listener

import android.util.Log
import com.stripe.aod.sampleapp.Config
import com.stripe.stripeterminal.external.callable.TerminalListener
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.external.models.PaymentStatus
import com.stripe.stripeterminal.external.models.Reader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * The `TerminalEventListener` implements the [TerminalListener] interface and will
 * forward along any events to other parts of the app that register for updates.
 *
 */
object TerminalEventListener : TerminalListener {
    private val _onUnexpectedReaderDisconnect = MutableSharedFlow<Reader>()
    private val _onConnectionStatusChange = MutableSharedFlow<ConnectionStatus>()
    private val _onPaymentStatusChange = MutableSharedFlow<PaymentStatus>()

    val onUnexpectedReaderDisconnect = _onUnexpectedReaderDisconnect.asSharedFlow()
    val onConnectionStatusChange = _onConnectionStatusChange.asSharedFlow()
    val onPaymentStatusChange = _onPaymentStatusChange.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onUnexpectedReaderDisconnect(reader: Reader) {
        Log.i(
            Config.TAG,
            "onUnexpectedReaderDisconnect Reader serial number: ${reader.serialNumber}"
        )

        scope.launch {
            _onUnexpectedReaderDisconnect.emit(reader)
        }
    }

    override fun onConnectionStatusChange(status: ConnectionStatus) {
        Log.i(Config.TAG, "onConnectionStatusChange: $status")

        scope.launch {
            _onConnectionStatusChange.emit(status)
        }
    }

    override fun onPaymentStatusChange(status: PaymentStatus) {
        Log.i(Config.TAG, "onPaymentStatusChange: $status")

        scope.launch {
            _onPaymentStatusChange.emit(status)
        }

    }
}
