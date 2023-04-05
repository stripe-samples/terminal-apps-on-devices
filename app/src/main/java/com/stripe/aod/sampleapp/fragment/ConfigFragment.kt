package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.databinding.FragmentConfigBinding

class ConfigFragment : Fragment(R.layout.fragment_config) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get viewBinding instance
        val viewBinding = FragmentConfigBinding.bind(view)

        viewBinding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        // hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(
            activity as MainActivity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            },
        )
    }
}
