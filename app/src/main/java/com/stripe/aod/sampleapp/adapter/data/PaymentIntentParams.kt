package com.stripe.aod.sampleapp.adapter.data

data class PaymentIntentParams(
    val amount: Int,
    val currency: String,
    val requestExtendedAuthorization: Boolean = true,
    val requestIncrementalAuthorizationSupport: Boolean = true
)

fun PaymentIntentParams.toMap(): Map<String, String> {
    return mapOf(
        "amount" to amount.toString(),
        "currency" to currency,
        "payment_method_options[card_present[request_extended_authorization]]" to requestExtendedAuthorization.toString(),
        "payment_method_options[card_present[request_incremental_authorization_support]]" to requestIncrementalAuthorizationSupport.toString()
    )
}
