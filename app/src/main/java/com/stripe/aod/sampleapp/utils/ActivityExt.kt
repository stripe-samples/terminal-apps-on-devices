package com.stripe.aod.sampleapp.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.stripe.aod.sampleapp.R

fun FragmentActivity.navigateToTarget(
    tag: String,
    fragment: Fragment,
    replace: Boolean = true,
    addToBackStack: Boolean = false,
) {
    val frag = supportFragmentManager.findFragmentByTag(tag) ?: fragment
    supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(
            R.anim.slide_right_in,
            R.anim.slide_left_out,
            R.anim.slide_left_in,
            R.anim.slide_right_out,
        )
        .apply {
            if (replace) {
                replace(R.id.container, frag, tag)
            } else {
                add(R.id.container, frag, tag)
            }

            if (addToBackStack) {
                addToBackStack(tag)
            }
        }
        .commit()
}
