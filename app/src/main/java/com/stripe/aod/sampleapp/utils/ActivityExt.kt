package com.stripe.aod.sampleapp.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.jakewharton.rxbinding4.view.clicks
import com.stripe.aod.sampleapp.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun navOptions(): NavOptions {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.slide_right_in)
        .setExitAnim(R.anim.slide_left_out)
        .setPopEnterAnim(R.anim.slide_left_in)
        .setPopExitAnim(R.anim.slide_right_out)
        .build()
}

fun formatCentsToString(amount: Int): String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(amount / 100.0)
}

inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
): Job {
    return viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

fun Fragment.backToHome() {
    findNavController().navigate(
        R.id.homeFragment,
        null,
        NavOptions.Builder()
            .setPopUpTo(R.id.inputFragment, true)
            .build()
    )
}

fun EditText.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.setThrottleClickListener(intervalDuration: Long = 1000, block: (view: View) -> Unit) {
    clicks()
        .throttleLast(intervalDuration, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { block.invoke(this) }
}
