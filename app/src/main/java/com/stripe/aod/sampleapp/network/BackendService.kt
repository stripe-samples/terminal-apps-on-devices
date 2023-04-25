package com.stripe.aod.sampleapp.network

import com.stripe.aod.sampleapp.data.PaymentIntentCreationResponse
import com.stripe.aod.sampleapp.model.ConnectionToken
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * The `BackendService` interface handles the calls we need to make to our backend.
 */
interface BackendService {

    /**
     * Get a connection token string from the backend
     */
    @POST("connection_token")
    suspend fun getConnectionToken(): ConnectionToken

    @FormUrlEncoded
    @POST("create_payment_intent")
    suspend fun createPaymentIntent(@FieldMap createPaymentIntentParams: Map<String, String>): PaymentIntentCreationResponse?

    @FormUrlEncoded
    @POST("update_payment_intent")
    suspend fun updatePaymentIntent(@FieldMap updatePaymentIntentParams: Map<String, String>): PaymentIntentCreationResponse?

    @FormUrlEncoded
    @POST("capture_payment_intent")
    suspend fun capturePaymentIntent(@Field("payment_intent_id") id: String): PaymentIntentCreationResponse?
}
