package com.stripe.aod.sampleapp.fragment

import android.content.ActivityNotFoundException
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentSettingsBinding
import com.stripe.aod.sampleapp.model.SettingsDeepLink
import com.stripe.aod.sampleapp.utils.setThrottleClickListener

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentSettingsBinding.bind(view)

        viewBinding.back.setThrottleClickListener {
            findNavController().navigateUp()
        }

        viewBinding.itemSettings.setThrottleClickListener {
            launch(viewBinding, SettingsDeepLink.OVERALL_SETTINGS)
        }

        viewBinding.itemRegulatory.setThrottleClickListener {
            launch(viewBinding, SettingsDeepLink.REGULATORY)
        }

        viewBinding.itemAppearance.setThrottleClickListener {
            launch(viewBinding, SettingsDeepLink.APPEARANCE)
        }

        viewBinding.itemLanguage.setThrottleClickListener {
            launch(viewBinding, SettingsDeepLink.LANGUAGE)
        }

        viewBinding.itemAdvancedSettings.setThrottleClickListener {
            launch(viewBinding, SettingsDeepLink.ADVANCED_SETTINGS)
        }

        viewBinding.itemNetwork.setThrottleClickListener {
            launch(viewBinding, SettingsDeepLink.NETWORK_SETTINGS)
        }
    }

    private fun launch(viewBinding: FragmentSettingsBinding, deepLink: SettingsDeepLink) {
        try {
            startActivity(deepLink.toIntent())
        } catch (e: ActivityNotFoundException) {
            // Should not happen on Devkits or Production devices. However, on an emulator or
            // other device without Stripe apps installed, no app exists to receive the intent
            // Surface a message instead of crashing.
            Snackbar.make(
                viewBinding.coordinatorLayout,
                R.string.error_settings_unavailable,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}
