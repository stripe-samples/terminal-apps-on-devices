package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentReceiptBinding
import com.stripe.aod.sampleapp.utils.backToHome
import com.stripe.aod.sampleapp.utils.formatCentsToString
import com.stripe.aod.sampleapp.utils.navOptions

class ReceiptFragment : Fragment(R.layout.fragment_receipt) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentReceiptBinding.bind(view)

        val amount = arguments?.let {
            ReceiptFragmentArgs.fromBundle(it).amount
        } ?: 0

        val paymentIntentId = arguments?.let {
            ReceiptFragmentArgs.fromBundle(it).paymentIntentID
        }

        viewBinding.totalAmount.text = formatCentsToString(amount)
        viewBinding.receiptEmail.setOnClickListener {
            findNavController().navigate(
                EmailFragmentDirections.actionEmailFragmentToReceiptFragment(
                    paymentIntentId!!,
                    amount
                ),
                navOptions()
            )
        }

        viewBinding.receiptSkip.setOnClickListener {
            backToHome()
        }
    }
}
