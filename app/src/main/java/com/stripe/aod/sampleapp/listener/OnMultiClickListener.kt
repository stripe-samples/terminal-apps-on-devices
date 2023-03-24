package com.stripe.aod.sampleapp.listener

import android.view.View

/**
 * Prevent duplicate clicks
 */
abstract class OnMultiClickListener : View.OnClickListener {

    abstract fun onMultiClick(v: View?)

    override fun onClick(v: View) {
        val curClickTime = System.currentTimeMillis()
        if (curClickTime - lastClickTime >= MIN_CLICK_DELAY_TIME) {
            // Reset lastClickTime to the current click time when the click interval is over
            lastClickTime = curClickTime
            onMultiClick(v)
            lastClickTime = 0L
        }
    }

    companion object {
        // The interval between two button clicks must be no less than 1000 milliseconds
        private const val MIN_CLICK_DELAY_TIME = 1000
        private var lastClickTime: Long = 0
    }
}