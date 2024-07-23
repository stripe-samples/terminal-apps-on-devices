package com.stripe.aod.sampleapp.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.ContextThemeWrapper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.listener.TerminalEventListener
import com.stripe.aod.sampleapp.model.MainViewModel
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.models.TerminalException
import com.stripe.stripeterminal.log.LogLevel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::onPermissionResult
    )

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissionsIfNecessary()
    }

    private fun requestPermissionsIfNecessary() {
        if (Build.VERSION.SDK_INT >= 31) {
            requestPermissionsIfNecessarySdk31()
        } else {
            requestPermissionsIfNecessarySdkBelow31()
        }
    }

    private fun isGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionsIfNecessarySdkBelow31() {
        // Check for location permissions
        if (!isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // If we don't have them yet, request them before doing anything else
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        } else if (!Terminal.isInitialized() && verifyGpsEnabled()) {
            initialize()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermissionsIfNecessarySdk31() {
        // Check for location and bluetooth permissions
        val deniedPermissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
            .filterNot(::isGranted)
            .toTypedArray()

        if (deniedPermissions.isNotEmpty()) {
            // If we don't have them yet, request them before doing anything else
            requestPermissionLauncher.launch(deniedPermissions)
        } else if (!Terminal.isInitialized() && verifyGpsEnabled()) {
            initialize()
        }
    }

    private fun onPermissionResult(result: Map<String, Boolean>) {
        val deniedPermissions: List<String> = result
            .filter { !it.value }
            .map { it.key }

        // If we receive a response to our permission check, initialize
        if (deniedPermissions.isEmpty() && !Terminal.isInitialized() && verifyGpsEnabled()) {
            initialize()
        }
    }

    /**
     * Initialize the [Terminal]
     */
    private fun initialize() {
        // Initialize the Terminal as soon as possible
        try {
            Terminal.initTerminal(
                applicationContext,
                LogLevel.VERBOSE,
                viewModel.tokenProvider,
                TerminalEventListener,
            )

            viewModel.discoveryReaders()
        } catch (e: TerminalException) {
            throw RuntimeException(
                "Location services are required in order to initialize the Terminal.",
                e
            )
        }
    }

    private fun verifyGpsEnabled(): Boolean {
        val locationManager: LocationManager? =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

        val gpsEnabled = runCatching {
            locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        }.getOrDefault(false)

        if (!gpsEnabled) {
            // notify user
            AlertDialog.Builder(
                ContextThemeWrapper(
                    this,
                    com.google.android.material.R.style.Theme_MaterialComponents_DayNight_DarkActionBar
                )
            )
                .setMessage("Please enable location services")
                .setCancelable(false)
                .setPositiveButton("Open location settings") { _, _ ->
                    this.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .create()
                .show()
        }

        return gpsEnabled
    }
}
