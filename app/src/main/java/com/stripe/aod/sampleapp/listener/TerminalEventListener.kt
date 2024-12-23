package com.stripe.aod.sampleapp.listener

import android.util.Log
import com.stripe.aod.sampleapp.Config
import com.stripe.stripeterminal.external.callable.TerminalListener
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.external.models.PaymentStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * The `TerminalEventListener` implements the [TerminalListener] interface and will
 * forward along any events to other parts of the app that register for updates.
 *
 */
object TerminalEventListener : TerminalListener {
    private val _onConnectionStatusChange = MutableSharedFlow<ConnectionStatus>()
    private val _onPaymentStatusChange = MutableSharedFlow<PaymentStatus>()

    val onConnectionStatusChange: Flow<ConnectionStatus> = _onConnectionStatusChange.asSharedFlow()
    val onPaymentStatusChange: Flow<PaymentStatus> = _onPaymentStatusChange.asSharedFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

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
