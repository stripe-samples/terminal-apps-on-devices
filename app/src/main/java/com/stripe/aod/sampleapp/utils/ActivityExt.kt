package com.stripe.aod.sampleapp.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.stripe.aod.sampleapp.R
import java.text.DecimalFormat


/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 */
fun FragmentActivity.replaceFragmentInActivity(fragment: Fragment, @IdRes frameId: Int) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}

/**
 * The `fragment` is added to the container view with tag. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.addFragmentToActivity(fragment: Fragment, tag: String) {
    supportFragmentManager.transact {
        add(fragment, tag)
    }
}

fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}

/**
 * Runs a FragmentTransaction, then calls commit().
 */
inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().setCustomAnimations(
        R.anim.slide_right_in,
        R.anim.slide_left_out,
        R.anim.slide_left_in,
        R.anim.slide_right_out)
    .apply {
        action()
    }.commit()
}

/**
 * Navigate to the given fragment.
 *
 * @param fragment Fragment to navigate to.
 */
fun FragmentActivity.navigateToTarget(
    tag: String,
    fragment: Fragment,
    replace: Boolean = true,
    addToBackStack: Boolean = false
) {
    val frag = supportFragmentManager.findFragmentByTag(tag) ?: fragment
    supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(
            R.anim.slide_right_in,
            R.anim.slide_left_out,
            R.anim.slide_left_in,
            R.anim.slide_right_out)
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

/**
 * Navigate back to the previous fragment.
 */
fun FragmentActivity.backToPrevious() {
    supportFragmentManager.popBackStackImmediate()
}

/**
 * Remove given fragment from backstack by tag
 */
fun FragmentActivity.popBackStackByName(name: String) {
    supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

/**
 * Clear all backstack fragment
 */
fun FragmentActivity.clearBackStack() {
    val fm: FragmentManager = supportFragmentManager
    for (i in 0 until fm.backStackEntryCount) {
        fm.popBackStackImmediate()
    }
}

fun FragmentActivity.toast(message: String) {
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}

fun Fragment.toast(message: String) {
    Toast.makeText(activity,message,Toast.LENGTH_LONG).show()
}

fun Context.toast(@StringRes messageId: Int) {
    Toast.makeText(this,getString(messageId),Toast.LENGTH_LONG).show()
}

fun Fragment.formatAmount(amt: String?): String? {
    val decimalFormat = DecimalFormat("#,##0.00")
    return decimalFormat.format(amt!!.toBigDecimal())
}