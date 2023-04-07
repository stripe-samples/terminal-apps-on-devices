package com.stripe.aod.sampleapp.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.navigation.NavOptions
import com.stripe.aod.sampleapp.R
fun navOptions(): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.slide_right_in) 
        .setExitAnim(R.anim.slide_left_out) 
        .setPopEnterAnim(R.anim.slide_left_in) 
        .setPopExitAnim(R.anim.slide_right_out)
        .build()
}

fun Context.toast(@StringRes messageId: Int) {
    Toast.makeText(this, getString(messageId), Toast.LENGTH_LONG).show()
}
