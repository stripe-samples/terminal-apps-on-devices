package com.example.fridgeapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fridgeapp.R
import com.example.fridgeapp.databinding.FragmentReceiptBinding
import com.example.fridgeapp.utils.backToHome
import com.example.fridgeapp.utils.formatCentsToString
import com.example.fridgeapp.utils.navOptions
import com.example.fridgeapp.utils.setThrottleClickListener

class ReceiptFragment : Fragment(R.layout.fragment_receipt) {
    private val args: ReceiptFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentReceiptBinding.bind(view)

        viewBinding.totalAmount.text = formatCentsToString(args.amount)
        viewBinding.receiptPrint.isEnabled = false
        viewBinding.receiptSms.isEnabled = false
        viewBinding.receiptEmail.setThrottleClickListener {
            findNavController()
                    .navigate(
                            ReceiptFragmentDirections.actionReceiptFragmentToEmailFragment(
                                    args.paymentIntentID
                            ),
                            navOptions()
                    )
        }

        viewBinding.receiptSkip.setOnClickListener { backToHome() }
    }
}
