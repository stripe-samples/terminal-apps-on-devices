package com.stripe.aod.sampleapp.network

import android.util.Log
import com.stripe.aod.sampleapp.BuildConfig
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.data.PaymentIntentCreationResponse
import com.stripe.stripeterminal.external.models.ConnectionTokenException
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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
    internal suspend fun createConnectionToken(
        canRetry: Boolean,
    ): String {
        return try {
            val result = service.getConnectionToken()

            result.secret.ifEmpty {
                throw ConnectionTokenException("Empty connection token.")
            }
        } catch (e: Exception) {
            when (e) {
                is SocketTimeoutException,
                is TimeoutException,
                is IOException -> {
                    if (canRetry) {
                        Log.e(Config.TAG, "Error while creating connection token, retrying.", e)
                        createConnectionToken(canRetry = false)
                    } else {
                        throw ConnectionTokenException("Failed to create connection token.", e)
                    }
                }

                else -> {
                    throw ConnectionTokenException("Failed to create connection token.", e)
                }
            }
        }
    }

    suspend fun createPaymentIntent(createPaymentIntentParams: Map<String, String>): Result<PaymentIntentCreationResponse> =
        runCatching {
            val response = service.createPaymentIntent(createPaymentIntentParams.toMap())
            response ?: error("Failed to create PaymentIntent")
        }

    suspend fun updatePaymentIntent(updatePaymentIntentParams: Map<String, String>): Result<PaymentIntentCreationResponse> =
        runCatching {
            val response = service.updatePaymentIntent(updatePaymentIntentParams.toMap())
            response ?: error("Failed to update PaymentIntent")
        }

    suspend fun capturePaymentIntent(@Field("payment_intent_id") id: String): Result<PaymentIntentCreationResponse> =
        runCatching {
            val response = service.capturePaymentIntent(id)
            response ?: error("Failed to capture PaymentIntent")
        }
}
