package com.stripe.aod.sampleapp.model

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

/**
 * See https://docs.stripe.com/terminal/features/apps-on-devices/build for the most up-to-date
 * documentation about deeplinks.
 *
 */
enum class AdminPinConfiguration {
    /**
     * Some deeplinks can be used to re-register readers or modify network configuration.
     * These deeplinks always require AdminPin.
     * Note: "bypass_admin_menu_passcode" arguments are silently ignored.
      */
    ADMIN_PIN_ALWAYS_REQUIRED,

    /**
     * Some deeplinks are reguired to be shown to all customers regardless of role / access.
     * Note: "bypass_admin_menu_passcode" arguments are silently ignored.
     */
    ADMIN_PIN_NEVER_REQUIRED,

    /**
     * For the rest of the deeplinks, whether the AdminPin is shown is configurable by passing
     * a boolean intent extra with key "bypass_admin_menu_passcode".
     * If no argument is provided, the AdminPin will be shown.
     */
    ADMIN_PIN_CONFIGURABLE,
}

enum class SettingsDeepLink(
    private val uri : Uri,
    private val adminPinConfiguration: AdminPinConfiguration
) {
    OVERALL_SETTINGS (
        "stripe://settings/".toUri(),
        AdminPinConfiguration.ADMIN_PIN_ALWAYS_REQUIRED,
    ),
    ADVANCED_SETTINGS (
        "stripe://settings/advanced/".toUri(),
        AdminPinConfiguration.ADMIN_PIN_ALWAYS_REQUIRED,
    ),
    NETWORK_SETTINGS (
        "stripe://settings/network/".toUri(),
        AdminPinConfiguration.ADMIN_PIN_ALWAYS_REQUIRED,
    ),
    REGULATORY(
        "stripe://settings/regulatory/".toUri(),
        AdminPinConfiguration.ADMIN_PIN_NEVER_REQUIRED,
    ),
    APPEARANCE(
        "stripe://settings/appearance/".toUri(),
        AdminPinConfiguration.ADMIN_PIN_CONFIGURABLE,
    ),
    LANGUAGE(
        "stripe://settings/language/".toUri(),
        AdminPinConfiguration.ADMIN_PIN_CONFIGURABLE,
    );

    fun toIntent(): Intent {
        return Intent(Intent.ACTION_VIEW).setData(uri).also {
            // For this demo app, don't show AdminPin for deeplinks that support it.
            // Different integrations may choose to show
            if (adminPinConfiguration == AdminPinConfiguration.ADMIN_PIN_CONFIGURABLE) {
                it.putExtra("bypass_admin_menu_passcode", true)
            }
        }

    }
}