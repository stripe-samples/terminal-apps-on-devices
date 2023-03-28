package com.stripe.aod.sampleapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stripe.stripeterminal.external.callable.Cancelable
import com.stripe.stripeterminal.external.models.Reader

class DiscoveryViewModel : ViewModel() {
    val readers: MutableLiveData<List<Reader>> = MutableLiveData(emptyList())
    var discoveryTask: Cancelable? = null
}
