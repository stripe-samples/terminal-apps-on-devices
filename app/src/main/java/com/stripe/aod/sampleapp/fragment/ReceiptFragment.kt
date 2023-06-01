package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.data.EmailReceiptParams
import com.stripe.aod.sampleapp.databinding.FragmentReceiptBinding
import com.stripe.aod.sampleapp.model.CheckoutViewModel
import com.stripe.aod.sampleapp.utils.backToHome
import com.stripe.aod.sampleapp.utils.formatCentsToString
import com.stripe.aod.sampleapp.utils.navOptions
import com.stripe.aod.sampleapp.utils.setThrottleClickListener

class ReceiptFragment : Fragment(R.layout.fragment_receipt) {
    private val args: ReceiptFragmentArgs by navArgs()
    private val viewModel by viewModels<CheckoutViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentReceiptBinding.bind(view)

        viewBinding.totalAmount.text = formatCentsToString(args.amount)
        viewBinding.receiptPrint.isEnabled = false
        viewBinding.receiptSms.isEnabled = false
        viewBinding.receiptEmail.setThrottleClickListener {
            findNavController().navigate(
                ReceiptFragmentDirections.actionReceiptFragmentToEmailFragment(
                    args.paymentIntentID
                ),
                navOptions()
            )
        }

        viewBinding.receiptSkip.setThrottleClickListener {
            viewBinding.receiptSkip.isEnabled = false

            viewModel.updateReceiptEmailPaymentIntent(
                EmailReceiptParams(
                    paymentIntentId = args.paymentIntentID,
                    receiptEmail = ""
                ),
                successCallback = {
                    backToHome()
                },
                failCallback = { message ->
                    viewBinding.receiptSkip.isEnabled = true
                    Snackbar.make(
                        viewBinding.receiptSkip,
                        if (message.isNullOrEmpty()) {
                            getString(R.string.error_fail_to_capture_payment_intent)
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
