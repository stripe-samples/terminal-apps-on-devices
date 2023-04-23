package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentCheckoutBinding

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get viewBinding instance
        val viewBinding = FragmentCheckoutBinding.bind(view)

        viewBinding.back.setOnClickListener {
            findNavController().navigateUp()
        }
        // hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )

        viewBinding.back.setOnClickListener { findNavController().navigateUp() }
        viewBinding.submit.setOnClickListener { startPaymentIntent() }
    }

    private fun startPaymentIntent() {
        // TODO: goto create new payment
    }
}
