package com.stripe.aod.sampleapp.network

import com.stripe.stripeterminal.external.callable.ConnectionTokenCallback
import com.stripe.stripeterminal.external.callable.ConnectionTokenProvider
import com.stripe.stripeterminal.external.models.ConnectionTokenException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * A simple implementation of the [ConnectionTokenProvider] interface. We just request a
 * new token from our backend simulator and forward any exceptions along to the SDK.
 */
class TokenProvider(private val coroutineScope: CoroutineScope) : ConnectionTokenProvider {
    override fun fetchConnectionToken(callback: ConnectionTokenCallback) {
        coroutineScope.launch {
            try {
                val token = ApiClient.createConnectionToken(canRetry = true)
                callback.onSuccess(token)
            } catch (e: ConnectionTokenException) {
                callback.onFailure(e)
            }
        }
    }
}
