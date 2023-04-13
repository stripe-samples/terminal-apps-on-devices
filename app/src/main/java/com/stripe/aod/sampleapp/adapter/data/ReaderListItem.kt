package com.stripe.aod.sampleapp.adapter.data

import com.stripe.stripeterminal.external.models.Reader

data class ReaderListItem(val reader: Reader, val isConnected: Boolean = false)
