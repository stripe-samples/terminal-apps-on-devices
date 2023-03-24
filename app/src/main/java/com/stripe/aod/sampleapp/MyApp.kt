package com.stripe.aod.sampleapp

import android.app.Application
import com.stripe.stripeterminal.TerminalApplicationDelegate.onCreate
import com.stripe.stripeterminal.TerminalApplicationDelegate.onTrimMemory

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        onCreate(this)
        instance = this
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        onTrimMemory(this, level)
    }

    companion object {
        lateinit var instance: MyApp
    }
}