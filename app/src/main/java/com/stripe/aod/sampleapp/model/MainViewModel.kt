package com.stripe.aod.sampleapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.aod.sampleapp.network.TokenProvider

class MainViewModel : ViewModel() {
    val tokenProvider = TokenProvider(viewModelScope)
}
