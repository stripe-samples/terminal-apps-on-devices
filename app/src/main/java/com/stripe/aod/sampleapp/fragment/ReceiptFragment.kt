package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentReceiptBinding

class ReceiptFragment : Fragment(R.layout.fragment_receipt) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewBinding = FragmentReceiptBinding.bind(view)

        viewBinding.receiptSkip.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
