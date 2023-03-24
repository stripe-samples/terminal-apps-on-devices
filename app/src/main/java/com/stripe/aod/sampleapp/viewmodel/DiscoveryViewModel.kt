package com.stripe.aod.sampleapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stripe.aod.sampleapp.listener.ReaderClickListener
import com.stripe.aod.sampleapp.listener.ReaderConnectStatusListener
import com.stripe.stripeterminal.external.callable.Cancelable
import com.stripe.stripeterminal.external.models.DiscoveryMethod
import com.stripe.stripeterminal.external.models.Location
import com.stripe.stripeterminal.external.models.Reader

class DiscoveryViewModel(val discoveryMethod: DiscoveryMethod) : ViewModel() {
    val readers: MutableLiveData<List<Reader>> = MutableLiveData(listOf())
    val isConnecting: MutableLiveData<Boolean> = MutableLiveData(false)
    val isUpdating: MutableLiveData<Boolean> = MutableLiveData(false)
    val updateProgress: MutableLiveData<Float> = MutableLiveData(0F)
    val selectedLocation = MutableLiveData<Location?>(null)
    var discoveryTask: Cancelable? = null

    var readerClickListener: ReaderClickListener? = null
    var readerConnectStatusListener: ReaderConnectStatusListener? = null
}
