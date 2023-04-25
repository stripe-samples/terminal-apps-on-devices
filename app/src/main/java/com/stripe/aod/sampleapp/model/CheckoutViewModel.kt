package com.stripe.aod.sampleapp.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.data.CreatePaymentParams
import com.stripe.aod.sampleapp.data.EmailReceiptParams
import com.stripe.aod.sampleapp.data.PaymentIntentCreationResponse
import com.stripe.aod.sampleapp.data.toMap
import com.stripe.aod.sampleapp.network.ApiClient
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback
import com.stripe.stripeterminal.external.models.CollectConfiguration
import com.stripe.stripeterminal.external.models.PaymentIntent
import com.stripe.stripeterminal.external.models.TerminalException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckoutViewModel : ViewModel() {
    private var paymentIntentID: String? = null

    fun createPaymentIntent(
        createPaymentParams: CreatePaymentParams,
        successCallback: (String) -> Unit,
        failCallback: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = createPaymentIntent(createPaymentParams.toMap())
                if (result) {
                    paymentIntentID?.let { successCallback(it) }
                } else {
                    failCallback("Failed to create payment intent")
                }
            } catch (e: Exception) {
                failCallback(e.message)
            }
        }
    }

    fun updateEmailReceiptPaymentIntent(
        emailReceiptParams: EmailReceiptParams,
        successCallback: (String) -> Unit,
        failCallback: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = updatePaymentIntent(emailReceiptParams.toMap())
                if (result) {
                    successCallback("Update payment intent success")
                } else {
                    failCallback("Failed to update payment intent")
                }
            } catch (e: Exception) {
                failCallback(e.message)
            }
        }
    }

    private suspend fun createPaymentIntent(createPaymentIntentParams: Map<String, String>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val captureResult = createAndProcessPaymentIntent(createPaymentIntentParams)
                if (!captureResult) {
                    error("Failed to create payment intent")
                }
                true
            } catch (e: Exception) {
                false
            }
        }

    private suspend fun updatePaymentIntent(updatePaymentIntentParams: Map<String, String>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val updateResult = updateAndProcessPaymentIntent(updatePaymentIntentParams)
                if (!updateResult) {
                    error("Failed to update payment intent")
                }
                true
            } catch (e: Exception) {
                false
            }
        }

    private suspend fun createAndProcessPaymentIntent(
        createPaymentIntentParams: Map<String, String>
    ): Boolean = when (
        val response =
            ApiClient.createPaymentIntent(createPaymentIntentParams).firstOrNull()?.getOrNull()
    ) {
        is PaymentIntentCreationResponse -> {
            val secret = response.secret
            val paymentIntent = retrievePaymentIntent(secret)
            paymentIntentID = paymentIntent.id
            val paymentIntentAfterCollect = collectPaymentInfo(paymentIntent)
            processPayment(paymentIntentAfterCollect)
            true
        }

        else -> false
    }

    private suspend fun updateAndProcessPaymentIntent(
        createPaymentIntentParams: Map<String, String>
    ): Boolean = when (
        val response =
            ApiClient.updatePaymentIntent(createPaymentIntentParams).firstOrNull()?.getOrNull()
    ) {
        is PaymentIntentCreationResponse -> {
            val secret = response.secret
            Log.d(Config.TAG, "updateAndProcessPaymentIntent secret : ${response.secret}")
            val paymentIntent = retrievePaymentIntent(secret)
            val captureResult = capturePaymentIntent(paymentIntent)
            captureResult
        }
        else -> false
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

    private suspend fun capturePaymentIntent(paymentIntent: PaymentIntent): Boolean {
        return suspendCoroutine { continuation ->
            try {
                ApiClient.capturePaymentIntent(paymentIntent.id)
                Log.d(Config.TAG, "capturePaymentIntent onSuccess ")
                continuation.resume(true)
            } catch (e: Exception) {
                continuation.resumeWith(Result.failure(e))
            }
        }
    }
}
