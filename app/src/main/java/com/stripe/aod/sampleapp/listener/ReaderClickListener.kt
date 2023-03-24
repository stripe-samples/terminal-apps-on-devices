package com.stripe.aod.sampleapp.listener

import android.util.Log
import android.widget.Toast
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.viewmodel.DiscoveryViewModel
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.callable.ReaderCallback
import com.stripe.stripeterminal.external.models.ConnectionConfiguration
import com.stripe.stripeterminal.external.models.DiscoveryMethod
import com.stripe.stripeterminal.external.models.Reader
import com.stripe.stripeterminal.external.models.TerminalException
import java.lang.ref.WeakReference

class ReaderClickListener(private val activityRef: WeakReference<MainActivity>, private val viewModel: DiscoveryViewModel) {

    fun onClick(reader: Reader) {

        val activity: MainActivity = activityRef.get() ?: return

        if (Terminal.getInstance().connectedReader != null) {
            //same one , skip
            if (reader.id === Terminal.getInstance().connectedReader!!.id) {
                Toast.makeText(
                    activity,
                    "Current Reader[ " + reader.id + " ] is Connected!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            //different reader , disconnect old first then connect new one again
            val lastReader: Reader? = Terminal.getInstance().connectedReader
            Terminal.getInstance().disconnectReader(object : Callback {
                override fun onSuccess() {
                    Log.d(Config.TAG, "Last Reader[ " + lastReader?.id + " ] disconnect success ")
                }

                override fun onFailure(e: TerminalException) {
                    Log.e(Config.TAG, "Last Reader[ " + lastReader?.id + " ] disconnect fail ")
                }
            })
        }

        Log.i(Config.TAG, "Connecting new Reader[ " + reader.id + " ] .... ")

        val readerCallback: ReaderCallback = object : ReaderCallback {
            override fun onSuccess(reader: Reader) {
                activityRef.get()?.runOnUiThread {
                    viewModel.isConnecting.value = false
                    viewModel.isUpdating.value = false
//                    activity.onConnectReader()
                }
            }

            override fun onFailure(e: TerminalException) {
                activityRef.get()?.runOnUiThread {
                    viewModel.isConnecting.value = false
                    viewModel.isUpdating.value = false
                }
            }
        }

        viewModel.isConnecting.value = true

        when (viewModel.discoveryMethod) {
            DiscoveryMethod.INTERNET -> {
                Terminal.getInstance().connectInternetReader(
                    reader,
                    ConnectionConfiguration.InternetConnectionConfiguration(),
                    readerCallback,
                )
            }
            DiscoveryMethod.HANDOFF -> Terminal.getInstance().connectHandoffReader(
                reader,
                ConnectionConfiguration.HandoffConnectionConfiguration(),
                null,
                readerCallback
            )
            else -> Log.w(javaClass.simpleName, "Trying to connect unsupported reader")
        }
    }
}
