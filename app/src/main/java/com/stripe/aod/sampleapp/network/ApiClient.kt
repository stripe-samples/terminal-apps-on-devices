package com.stripe.aod.sampleapp.network

import com.stripe.aod.sampleapp.BuildConfig
import com.stripe.aod.sampleapp.data.PaymentIntentCreationResponse
import com.stripe.stripeterminal.external.models.ConnectionTokenException
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field

object ApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BACKEND_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: BackendService = retrofit.create(BackendService::class.java)

    @Throws(ConnectionTokenException::class)
    internal suspend fun createConnectionToken(): String {
        try {
            val result = service.getConnectionToken()

            if (result.secret.isNotEmpty()) {
                return result.secret
            } else {
                throw ConnectionTokenException("Creating connection token failed")
            }
        } catch (e: IOException) {
            throw ConnectionTokenException("Creating connection token failed", e)
        }
    }

    suspend fun createPaymentIntent(createPaymentIntentParams: Map<String, String>): Result<PaymentIntentCreationResponse?> = runCatching {
        val response = service.createPaymentIntent(createPaymentIntentParams.toMap())
        response ?: error("Failed to create payment intent")
    }

    suspend fun updatePaymentIntent(updatePaymentIntentParams: Map<String, String>): Result<PaymentIntentCreationResponse> = runCatching {
        val response = service.updatePaymentIntent(updatePaymentIntentParams.toMap())
        response ?: error("Failed to update payment intent")
    }

    suspend fun capturePaymentIntent(@Field("payment_intent_id") id: String): Result<PaymentIntentCreationResponse> = runCatching {
        val response = service.capturePaymentIntent(id)
        response ?: error("Failed to capture payment intent")
    }
}
