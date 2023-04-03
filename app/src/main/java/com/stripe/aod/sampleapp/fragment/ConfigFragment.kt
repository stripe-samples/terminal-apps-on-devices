package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.databinding.FragmentConfigBinding
import com.stripe.aod.sampleapp.utils.backToPrevious

class ConfigFragment : Fragment(R.layout.fragment_config) {
    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.ConfigFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get viewBinding instance
        val viewBinding = FragmentConfigBinding.bind(view)

        viewBinding.configBack.setOnClickListener {
            activity?.backToPrevious()
        }

        // hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(
            activity as MainActivity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.backToPrevious()
                }
            },
        )
    }
}
