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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class CheckoutViewModel : ViewModel() {
    private val _currentPaymentIntent = MutableStateFlow<PaymentIntent?>(null)
    val currentPaymentIntent = _currentPaymentIntent.asStateFlow()

    fun createPaymentIntent(
        createPaymentParams: CreatePaymentParams,
        successCallback: ((String) -> Unit)? = null,
        failCallback: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                createAndProcessPaymentIntent(createPaymentParams.toMap()).fold(
                    onSuccess = { paymentIntent ->
                        _currentPaymentIntent.update { paymentIntent }
                        successCallback?.let {
                            it(paymentIntent.id)
                        }
                    },
                    onFailure = {
                        failCallback("Failed to create PaymentIntent")
                    }
                )
            } catch (e: TerminalException) {
                failCallback(e.message)
            }
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

    fun updateReceiptEmailPaymentIntent(
        emailReceiptParams: EmailReceiptParams,
        successCallback: (String) -> Unit,
        failCallback: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                updateAndProcessPaymentIntent(emailReceiptParams.toMap()).fold(
                    onSuccess = {
                        successCallback("Update PaymentIntent success")
                    },
                    onFailure = {
                        failCallback("Failed to update PaymentIntent")
                    }
                )
            } catch (e: TerminalException) {
                failCallback(e.message)
            }
        }
    }

    private suspend fun updateAndProcessPaymentIntent(
        createPaymentIntentParams: Map<String, String>
    ): Result<Boolean> = ApiClient.updatePaymentIntent(createPaymentIntentParams).map { response ->
        val secret = response.secret
        val paymentIntent = retrievePaymentIntent(secret)
        capturePaymentIntent(paymentIntent).isSuccess
    }

    private suspend fun capturePaymentIntent(paymentIntent: PaymentIntent) =
        ApiClient.capturePaymentIntent(paymentIntent.id)
}
