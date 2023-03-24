package com.stripe.aod.sampleapp.viewmodel

import androidx.lifecycle.ViewModel

class ConfigViewModel : ViewModel() {
    val extendedAuth: Boolean = false
    val incrementalAuth: Boolean = false
    val currency: String = "usd";
}
