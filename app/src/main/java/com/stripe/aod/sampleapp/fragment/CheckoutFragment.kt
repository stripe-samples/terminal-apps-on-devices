package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.data.CreatePaymentParams
import com.stripe.aod.sampleapp.databinding.FragmentCheckoutBinding
import com.stripe.aod.sampleapp.model.CheckoutViewModel
import com.stripe.aod.sampleapp.utils.formatCentsToString
import com.stripe.aod.sampleapp.utils.launchAndRepeatWithViewLifecycle
import com.stripe.aod.sampleapp.utils.navOptions
import com.stripe.stripeterminal.external.models.PaymentIntentStatus

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {
    private val checkoutViewModel by viewModels<CheckoutViewModel>()
    private val args: CheckoutFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get viewBinding instance
        val viewBinding = FragmentCheckoutBinding.bind(view)

        viewBinding.amount.text = formatCentsToString(args.amount)
        viewBinding.amountDescription.text = formatCentsToString(args.amount)

        // hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )

        launchAndRepeatWithViewLifecycle {
            checkoutViewModel.currentPaymentIntent.collect { paymentIntent ->
                paymentIntent?.takeIf {
                    it.status == PaymentIntentStatus.REQUIRES_CAPTURE
                }?.let {
                    findNavController().navigate(
                        CheckoutFragmentDirections.actionCheckoutFragmentToReceiptFragment(
                            paymentIntentID = it.id,
                            amount = args.amount
                        ),
                        navOptions()
                    )
                }
            }
        }

        viewBinding.back.setOnClickListener { findNavController().navigateUp() }
        viewBinding.submit.setOnClickListener {
            viewBinding.submit.isEnabled = false

            checkoutViewModel.createPaymentIntent(
                CreatePaymentParams(amount = args.amount, currency = "usd")
            ) { message ->
                Snackbar.make(
                    viewBinding.root,
                    if (message.isNullOrEmpty()) {
                        getString(R.string.error_fail_to_create_payment_intent)
                    } else {
                        message
                    },
                    Snackbar.LENGTH_SHORT
                ).show()
                viewBinding.submit.isEnabled = true
            }
        }
    }
}
