package com.stripe.aod.sampleapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.MyApp
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.utils.navigateToTarget
import com.stripe.stripeterminal.Terminal
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment: Fragment(R.layout.fragment_home),View.OnClickListener {

    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.HomeFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_settings.setOnClickListener(this)
        button_1.setOnClickListener(this)
        iv_readers.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.iv_settings) { // to stripe setting
            try {
                startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("stripe://settings/")))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (id == R.id.button_1) {
            if (Terminal.getInstance().connectedReader == null ) {
                Toast.makeText(activity, MyApp.instance.resources.getString(R.string.please_select_reader), Toast.LENGTH_SHORT).show()
                return
            }

            activity?.navigateToTarget(InputFragment.TAG, InputFragment(), true, true)
        } else if (id == R.id.iv_readers) {
            activity?.navigateToTarget(ConfigFragment.TAG, ConfigFragment(),true,true)
        }
    }
}