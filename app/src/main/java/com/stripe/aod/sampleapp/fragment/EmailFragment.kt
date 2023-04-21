package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
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

        viewBinding.inputEdit.doAfterTextChanged {
            val isValidEmail = it?.toString()?.matches(emailRegex.toRegex()) ?: false
            viewBinding.emailSend.isEnabled = isValidEmail
            viewBinding.inputLayout.error = if (it.isNullOrEmpty() || isValidEmail) {
                ""
            } else {
                getString(R.string.invalid_email)
            }
        }
    }
}
