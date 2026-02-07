package com.example.fridgeapp.data

data class EmailReceiptParams(val paymentIntentId: String, val receiptEmail: String)

fun EmailReceiptParams.toMap(): Map<String, String> {
    return mapOf("payment_intent_id" to paymentIntentId, "receipt_email" to receiptEmail)
}
