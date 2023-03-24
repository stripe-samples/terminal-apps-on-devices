package com.stripe.aod.sampleapp.listener

import com.stripe.stripeterminal.external.models.Reader

/**
 * An `Activity` that should be notified when various navigation activities have been triggered
 */
interface ReaderConnectStatusListener {

    /**
     * Notify the `Activity` that the [Reader] has been disconnected
     */
    fun onDisconnectReader()

    /**
     * Notify the `Activity` that a [Reader] has been connected
     */
    fun onConnectReader()

}
