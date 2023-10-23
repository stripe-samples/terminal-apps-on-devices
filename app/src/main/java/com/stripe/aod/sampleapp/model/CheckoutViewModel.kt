package com.stripe.aod.sampleapp.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.data.CreatePaymentParams
import com.stripe.aod.sampleapp.data.EmailReceiptParams
import com.stripe.aod.sampleapp.data.toMap
import com.stripe.aod.sampleapp.network.ApiClient
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback
import com.stripe.stripeterminal.external.models.CollectConfiguration
import com.stripe.stripeterminal.external.models.PaymentIntent
import com.stripe.stripeterminal.external.models.TerminalException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CheckoutViewModel : ViewModel() {
    private val _currentPaymentIntent = MutableStateFlow<PaymentIntent?>(null)
    val currentPaymentIntent = _currentPaymentIntent.asStateFlow()

    fun createPaymentIntent(
        createPaymentParams: CreatePaymentParams,
        onFailure: (FailureMessage) -> Unit
    ) {
        viewModelScope.launch {
            createAndProcessPaymentIntent(createPaymentParams.toMap())
                .fold(
                    onSuccess = { paymentIntent ->
                        _currentPaymentIntent.update { paymentIntent }
                    },
                    onFailure = {
                        val failureMessage = if (it is TerminalException) {
                            it.errorMessage
                        } else {
                            it.message ?: "Failed to collect payment"
                        }.let(::FailureMessage)
                        onFailure(failureMessage)
                    }
                )
        }
    }

    private suspend fun createAndProcessPaymentIntent(
        createPaymentIntentParams: Map<String, String>
    ): Result<PaymentIntent> {
        return ApiClient.createPaymentIntent(createPaymentIntentParams)
            .mapCatching { response ->
                val secret = response.secret
                val paymentIntent = retrievePaymentIntent(secret)
                val paymentIntentAfterCollect = collectPaymentInfo(paymentIntent)
                processPayment(paymentIntentAfterCollect)
            }
    }

    private suspend fun retrievePaymentIntent(
        secret: String
    ): PaymentIntent = suspendCoroutine { continuation ->
        Terminal.getInstance().retrievePaymentIntent(
            secret,
            object : PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    continuation.resume(paymentIntent)
                }

                override fun onFailure(e: TerminalException) {
                    Log.e(Config.TAG, "retrievePaymentIntent failure", e)
                    continuation.resumeWith(Result.failure(e))
                }
            }
        )
    }

    private suspend fun collectPaymentInfo(
        paymentIntent: PaymentIntent
    ): PaymentIntent = suspendCoroutine { continuation ->
        Terminal.getInstance().collectPaymentMethod(
            paymentIntent,
            object : PaymentIntentCallback {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    continuation.resume(paymentIntent)
                }

                override fun onFailure(e: TerminalException) {
                    Log.e(Config.TAG, "collectPaymentMethod failure", e)
                    continuation.resumeWith(Result.failure(e))
                }
            },
            CollectConfiguration.Builder().skipTipping(false).build()
        )
    }

    private suspend fun processPayment(paymentIntent: PaymentIntent): PaymentIntent {
        return suspendCoroutine { continuation ->
            Terminal.getInstance().confirmPaymentIntent(
                paymentIntent,
                object : PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        Log.d(Config.TAG, "processPaymentCallback onSuccess ")
                        continuation.resume(paymentIntent)
                    }

                    override fun onFailure(e: TerminalException) {
                        Log.e(Config.TAG, "processPayment failure", e)
                        continuation.resumeWith(Result.failure(e))
                    }
                }
            )
        }
    }

    fun updateReceiptEmailPaymentIntent(
        emailReceiptParams: EmailReceiptParams,
        onSuccess: () -> Unit,
        onFailure: (FailureMessage) -> Unit
    ) {
        viewModelScope.launch {
            updateAndProcessPaymentIntent(emailReceiptParams.toMap()).fold(
                onSuccess = {
                    onSuccess()
                },
                onFailure = {
                    onFailure(
                        FailureMessage("Failed to update PaymentIntent")
                    )
                }
            )
        }
    }

    private suspend fun updateAndProcessPaymentIntent(
        createPaymentIntentParams: Map<String, String>
    ): Result<Boolean> =
        ApiClient.updatePaymentIntent(createPaymentIntentParams)
            .mapCatching { response ->
                val secret = response.secret
                val paymentIntent = retrievePaymentIntent(secret)
                capturePaymentIntent(paymentIntent).isSuccess
            }

    private suspend fun capturePaymentIntent(paymentIntent: PaymentIntent) =
        ApiClient.capturePaymentIntent(paymentIntent.id.orEmpty())
}
