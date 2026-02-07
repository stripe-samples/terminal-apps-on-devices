package com.example.fridgeapp.network

import android.util.Log
import com.example.fridgeapp.BuildConfig
import com.example.fridgeapp.Config
import com.example.fridgeapp.data.PaymentIntentCreationResponse
import com.example.fridgeapp.data.Product
import com.stripe.stripeterminal.external.models.ConnectionTokenException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field

object ApiClient {

    private val client =
            OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(
                            Interceptor { chain ->
                                val request =
                                        chain.request()
                                                .newBuilder()
                                                .addHeader(
                                                        "x-vercel-protection-bypass",
                                                        BuildConfig.VERCEL_AUTOMATION_BYPASS_SECRET
                                                )
                                                .build()
                                chain.proceed(request)
                            }
                    )
                    .build()

    private val retrofit: Retrofit =
            Retrofit.Builder()
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

            result.secret.ifEmpty { throw ConnectionTokenException("Empty connection token.") }
        } catch (e: Exception) {
            when (e) {
                is SocketTimeoutException, is TimeoutException, is IOException -> {
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

    suspend fun fetchProducts(): Result<List<Product>> = runCatching {
        service.getProducts().products
    }

    suspend fun createPaymentIntentJson(
            params: Map<String, String>
    ): Result<PaymentIntentCreationResponse> = runCatching {
        val response = service.createPaymentIntentJson(params)
        response ?: error("Failed to create PaymentIntent")
    }

    suspend fun createPaymentIntent(
            createPaymentIntentParams: Map<String, String>
    ): Result<PaymentIntentCreationResponse> = runCatching {
        val response = service.createPaymentIntent(createPaymentIntentParams.toMap())
        response ?: error("Failed to create PaymentIntent")
    }

    suspend fun updatePaymentIntent(
            updatePaymentIntentParams: Map<String, String>
    ): Result<PaymentIntentCreationResponse> = runCatching {
        val response = service.updatePaymentIntent(updatePaymentIntentParams.toMap())
        response ?: error("Failed to update PaymentIntent")
    }

    suspend fun capturePaymentIntent(
            @Field("payment_intent_id") id: String
    ): Result<PaymentIntentCreationResponse> = runCatching {
        val response = service.capturePaymentIntent(id)
        response ?: error("Failed to capture PaymentIntent")
    }
}
