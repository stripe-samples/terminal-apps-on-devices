package com.stripe.aod.sampleapp

import android.app.Application
import com.stripe.stripeterminal.TerminalApplicationDelegate

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        TerminalApplicationDelegate.onCreate(this)
    }
}
