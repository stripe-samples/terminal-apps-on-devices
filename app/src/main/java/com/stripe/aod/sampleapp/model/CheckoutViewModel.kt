package com.stripe.aod.sampleapp.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.data.CreatePaymentParams
import com.stripe.aod.sampleapp.data.toMap
import com.stripe.aod.sampleapp.network.ApiClient
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback
import com.stripe.stripeterminal.external.models.CollectConfiguration
import com.stripe.stripeterminal.external.models.PaymentIntent
import com.stripe.stripeterminal.external.models.TerminalException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {
    fun createPaymentIntent(
        createPaymentParams: CreatePaymentParams,
        successCallback: (String) -> Unit,
        failCallback: (String?) -> Unit
    ) {
        viewModelScope.launch {
            createAndProcessPaymentIntent(createPaymentParams.toMap()).fold(
                onSuccess = { paymentIntent ->
                    successCallback(paymentIntent.id)
                },
                onFailure = {
                    failCallback("Failed to create PaymentIntent")
                }
            )
        }
    }

    private suspend fun createAndProcessPaymentIntent(
        createPaymentIntentParams: Map<String, String>
    ): Result<PaymentIntent> {
        return ApiClient.createPaymentIntent(createPaymentIntentParams).map { response ->
            val secret = response?.secret
            val paymentIntent = retrievePaymentIntent(secret!!)
            val paymentIntentAfterCollect = collectPaymentInfo(paymentIntent)
            processPayment(paymentIntentAfterCollect)
        }
    }

    private suspend fun retrievePaymentIntent(secret: String): PaymentIntent {
        return suspendCoroutine { continuation ->
            Terminal.getInstance().retrievePaymentIntent(
                secret,
                object : PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        Log.d(Config.TAG, "retrievePaymentIntent onSuccess ")
                        continuation.resume(paymentIntent)
                    }

                    override fun onFailure(e: TerminalException) {
                        continuation.resumeWith(Result.failure(e))
                    }
                }
            )
        }
    }

    private suspend fun collectPaymentInfo(paymentIntent: PaymentIntent): PaymentIntent {
        return suspendCoroutine { continuation ->
            Terminal.getInstance().collectPaymentMethod(
                paymentIntent,
                object : PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        Log.d(Config.TAG, "collectPaymentMethodCallback onSuccess ")
                        continuation.resume(paymentIntent)
                    }

                    override fun onFailure(e: TerminalException) {
                        continuation.resumeWith(Result.failure(e))
                    }
                },
                CollectConfiguration.Builder().skipTipping(false).build()
            )
        }
    }

    private suspend fun processPayment(paymentIntent: PaymentIntent): PaymentIntent {
        return suspendCoroutine { continuation ->
            Terminal.getInstance().processPayment(
                paymentIntent,
                object : PaymentIntentCallback {
                    override fun onSuccess(paymentIntent: PaymentIntent) {
                        Log.d(Config.TAG, "processPaymentCallback onSuccess ")
                        continuation.resume(paymentIntent)
                    }

                    override fun onFailure(e: TerminalException) {
                        continuation.resumeWith(Result.failure(e))
                    }
                }
            )
        }
    }
}
