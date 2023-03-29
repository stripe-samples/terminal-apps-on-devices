package com.stripe.aod.sampleapp.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.stripe.aod.sampleapp.R

class MainActivity : AppCompatActivity(){
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::onPermissionResult
    )

    public override fun onCreate(savedInstanseBundle: Bundle?) {
        super.onCreate(savedInstanseBundle)
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
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermissionsIfNecessarySdk31() {
        // Check for location and bluetooth permissions
        val deniedPermissions = mutableListOf<String>().apply {
            if (!isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) add(Manifest.permission.ACCESS_FINE_LOCATION)
            if (!isGranted(Manifest.permission.BLUETOOTH_CONNECT)) add(Manifest.permission.BLUETOOTH_CONNECT)
            if (!isGranted(Manifest.permission.BLUETOOTH_SCAN)) add(Manifest.permission.BLUETOOTH_SCAN)
        }.toTypedArray()

        if (deniedPermissions.isNotEmpty()) {
            // If we don't have them yet, request them before doing anything else
            requestPermissionLauncher.launch(deniedPermissions)
        }
    }

    private fun onPermissionResult(result: Map<String, Boolean>) {
    }
}