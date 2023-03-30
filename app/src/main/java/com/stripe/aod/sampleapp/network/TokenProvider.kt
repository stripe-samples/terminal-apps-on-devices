package com.stripe.aod.sampleapp.network

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.stripe.stripeterminal.external.callable.ConnectionTokenCallback
import com.stripe.stripeterminal.external.callable.ConnectionTokenProvider
import com.stripe.stripeterminal.external.models.ConnectionTokenException
import kotlinx.coroutines.launch

/**
 * A simple implementation of the [ConnectionTokenProvider] interface. We just request a
 * new token from our backend simulator and forward any exceptions along to the SDK.
 */
class TokenProvider(private val lifecycleOwner: LifecycleOwner) : ConnectionTokenProvider {
    override fun fetchConnectionToken(callback: ConnectionTokenCallback) {
        try {
            lifecycleOwner.lifecycleScope.launch {
                val token = ApiClient.createConnectionToken()
                callback.onSuccess(token)
            }
        } catch (e: ConnectionTokenException) {
            callback.onFailure(e)
        }
    }
}
