package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.data.CreatePaymentParams
import com.stripe.aod.sampleapp.databinding.FragmentCheckoutBinding
import com.stripe.aod.sampleapp.model.CheckoutViewModel
import com.stripe.aod.sampleapp.utils.formatCentsToString

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get viewBinding instance
        val viewBinding = FragmentCheckoutBinding.bind(view)
        val checkoutViewModel by viewModels<CheckoutViewModel>()

        var amount = 0
        arguments?.let {
            amount = CheckoutFragmentArgs.fromBundle(it).amount
        }
        viewBinding.amount.text = formatCentsToString(amount)
        viewBinding.amountDescription.text = formatCentsToString(amount)

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
        viewBinding.submit.setOnClickListener {
            checkoutViewModel.createPaymentIntent(
                CreatePaymentParams(amount = amount, currency = "usd"),
                successCallBack = { paymentIntentId ->
                    Snackbar.make(viewBinding.root, paymentIntentId, Snackbar.LENGTH_SHORT).show()
                    // TODO: goto receipt fragment
                },
                failCallback = { message ->
                    Snackbar.make(
                        viewBinding.root,
                        if (message.isNullOrEmpty()) {
                            getString(R.string.error_fail_to_create_payment_intent)
                        } else {
                            message
                        },
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }
}
