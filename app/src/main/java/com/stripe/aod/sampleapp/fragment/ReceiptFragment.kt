package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentReceiptBinding
import com.stripe.aod.sampleapp.utils.navOptions

class ReceiptFragment : Fragment(R.layout.fragment_receipt) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentReceiptBinding.bind(view)

        viewBinding.receiptEmail.setOnClickListener {
            findNavController().navigate(
                R.id.action_receiptFragment_to_emailFragment,
                null,
                navOptions()
            )
        }

        viewBinding.receiptSkip.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
