package com.stripe.aod.sampleapp.network

import com.stripe.aod.sampleapp.model.ConnectionToken
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
}
