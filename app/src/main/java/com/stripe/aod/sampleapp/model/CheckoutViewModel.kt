package com.stripe.aod.sampleapp.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.data.EmailReceiptParams
import com.stripe.aod.sampleapp.data.toMap
import com.stripe.aod.sampleapp.network.ApiClient
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback
import com.stripe.stripeterminal.external.models.PaymentIntent
import com.stripe.stripeterminal.external.models.TerminalException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {

    fun updateEmailReceiptPaymentIntent(
        emailReceiptParams: EmailReceiptParams,
        successCallback: (String) -> Unit,
        failCallback: (String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = updateAndProcessPaymentIntent(emailReceiptParams.toMap())
            if (result) {
                successCallback("Update PaymentIntent success")
            } else {
                failCallback("Failed to update PaymentIntent")
            }
        }
    }

    private suspend fun updateAndProcessPaymentIntent(
        createPaymentIntentParams: Map<String, String>
    ): Boolean = ApiClient.updatePaymentIntent(createPaymentIntentParams).fold(
        onSuccess = { response ->
            val secret = response.secret
            Log.d(Config.TAG, "updateAndProcessPaymentIntent secret : ${response.secret}")
            val paymentIntent = retrievePaymentIntent(secret)
            capturePaymentIntent(paymentIntent).isSuccess
        },
        onFailure = {
            false
        }
    )

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

    private suspend fun capturePaymentIntent(paymentIntent: PaymentIntent) =
        ApiClient.capturePaymentIntent(paymentIntent.id)
}
