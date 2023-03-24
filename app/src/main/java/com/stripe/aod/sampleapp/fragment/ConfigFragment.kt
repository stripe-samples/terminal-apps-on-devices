package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.utils.backToPrevious
import com.stripe.aod.sampleapp.utils.navigateToTarget
import kotlinx.android.synthetic.main.fragment_config.*
import kotlinx.android.synthetic.main.fragment_config.rl_back

class ConfigFragment: Fragment(R.layout.fragment_config) {

    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.ConfigFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        discover_button.setOnClickListener {
            activity?.navigateToTarget(
                DiscoverReaderFragment.TAG,
                DiscoverReaderFragment(),
                true,
                true
            )
        }

        rl_back.setOnClickListener {
            activity?.backToPrevious()
        }

        //hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(activity as MainActivity,object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity?.backToPrevious()
            }
        })
    }
}