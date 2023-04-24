package com.stripe.aod.sampleapp.network

import com.stripe.aod.sampleapp.BuildConfig
import com.stripe.aod.sampleapp.data.PaymentIntentCreationResponse
import com.stripe.stripeterminal.external.models.ConnectionTokenException
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    fun createPaymentIntent(createPaymentIntentParams: Map<String, String>): Flow<Result<PaymentIntentCreationResponse?>> = flow {
        val response = service.createPaymentIntent(createPaymentIntentParams.toMap())
        val result = response ?: throw Exception("Failed to create payment intent")
        emit(Result.success(result))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    fun updatePaymentIntent(updatePaymentIntentParams: Map<String, String>): Flow<Result<PaymentIntentCreationResponse>> = flow {
        val response = service.updatePaymentIntent(updatePaymentIntentParams.toMap())
        val result = response ?: throw Exception("Failed to update payment intent")
        emit(Result.success(result))
    }.catch {
        emit(Result.failure(it))
    }.flowOn(Dispatchers.IO)

    fun capturePaymentIntent(id: String): Flow<Boolean> = flow {
        val response = service.capturePaymentIntent(id)
        emit(response.isSuccessful)
    }.catch {
        emit(false)
    }.flowOn(Dispatchers.IO)
}
