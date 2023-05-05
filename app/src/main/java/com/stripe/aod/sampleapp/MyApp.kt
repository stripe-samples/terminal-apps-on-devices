package com.stripe.aod.sampleapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.navigation.NavOptions
import com.stripe.aod.sampleapp.utils.handleGlobalException
import com.stripe.stripeterminal.TerminalApplicationDelegate

class MyApp : Application() {
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        TerminalApplicationDelegate.onCreate(this)
        registerActivityLifecycleCallbacks()
        handleGlobalUncaughtException()
    }

    private fun handleGlobalUncaughtException() {
        handleGlobalException(
            activityProvider = {
                currentActivity
            },
            navigateAction = { navController ->
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, true)
                    .build()
                navController.navigate(R.id.homeFragment, null, navOptions)
            }
        )
    }

    private fun registerActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity 
            }
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}
