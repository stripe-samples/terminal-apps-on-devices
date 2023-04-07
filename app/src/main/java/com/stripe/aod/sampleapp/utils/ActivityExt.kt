package com.stripe.aod.sampleapp.utils

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
