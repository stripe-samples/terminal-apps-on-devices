package com.example.fridgeapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgeapp.data.CreatePaymentParams
import com.example.fridgeapp.data.EmailReceiptParams
import com.example.fridgeapp.data.toMap
import com.example.fridgeapp.network.ApiClient
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.models.CollectPaymentIntentConfiguration
import com.stripe.stripeterminal.external.models.PaymentIntent
import com.stripe.stripeterminal.external.models.TerminalException
import com.stripe.stripeterminal.ktx.processPaymentIntent
import com.stripe.stripeterminal.ktx.retrievePaymentIntent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
                                val failureMessage =
                                        if (it is TerminalException) {
                                                    it.errorMessage
                                                } else {
                                                    it.message ?: "Failed to collect payment"
                                                }
                                                .let(::FailureMessage)
                                onFailure(failureMessage)
                            }
                    )
        }
    }

    private suspend fun createAndProcessPaymentIntent(
            createPaymentIntentParams: Map<String, String>
    ): Result<PaymentIntent> {
        return ApiClient.createPaymentIntent(createPaymentIntentParams).mapCatching { response ->
            val secret = response.secret

            val terminal = Terminal.getInstance()
            val paymentIntent = terminal.retrievePaymentIntent(secret)

            // We're using the processPaymentIntent as it combines both collect and confirm
            // calls. Separate collect and confirm calls are still available if you need to
            // inspect the payment intent or payment method before confirming your payment.
            terminal.processPaymentIntent(
                    collectConfig =
                            CollectPaymentIntentConfiguration.Builder().skipTipping(false).build(),
                    intent = paymentIntent
            )
        }
    }

    fun updateReceiptEmailPaymentIntent(
            emailReceiptParams: EmailReceiptParams,
            onSuccess: () -> Unit,
            onFailure: (FailureMessage) -> Unit
    ) {
        viewModelScope.launch {
            updateAndProcessPaymentIntent(emailReceiptParams.toMap())
                    .fold(
                            onSuccess = { onSuccess() },
                            onFailure = {
                                onFailure(FailureMessage("Failed to update PaymentIntent"))
                            }
                    )
        }
    }

    private suspend fun updateAndProcessPaymentIntent(
            createPaymentIntentParams: Map<String, String>
    ): Result<Boolean> =
            ApiClient.updatePaymentIntent(createPaymentIntentParams).mapCatching { response ->
                val secret = response.secret
                val paymentIntent = Terminal.getInstance().retrievePaymentIntent(secret)
                capturePaymentIntent(paymentIntent).isSuccess
            }

    private suspend fun capturePaymentIntent(paymentIntent: PaymentIntent) =
            ApiClient.capturePaymentIntent(paymentIntent.id.orEmpty())
}
