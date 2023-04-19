package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentEmailBinding

class EmailFragment : Fragment(R.layout.fragment_email) {
    private val emailRegex = "^[A-Za-z\\d+_.-]+@[A-Za-z\\d.-]+\$"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewBinding = FragmentEmailBinding.bind(view)

        viewBinding.back.setOnClickListener {
            findNavController().navigateUp()
        }

        viewBinding.emailSend.setOnClickListener {
            if (!viewBinding.emailInput.text.matches(emailRegex.toRegex())) {
                // TODO: SnackBar to prompt
            } else {
                // TODO: goto update PaymentIntent's receipt_email
            }
        }

        viewBinding.emailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(input: Editable?) {
                viewBinding.emailSend.run {
                    visibility = if (input.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                    isEnabled = !input.isNullOrEmpty()
                }
            }
        })
    }
}
