package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentEmailBinding
import com.stripe.aod.sampleapp.utils.backToPrevious
import com.stripe.aod.sampleapp.utils.clearBackStack
import com.stripe.aod.sampleapp.utils.replaceFragmentInActivity

class EmailFragment : Fragment(R.layout.fragment_email) {
    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.EmailFragment"
    }

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    private var _viewBinding : FragmentEmailBinding? = null
    private val viewBinding get() = _viewBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentEmailBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.rlBack.setOnClickListener {
            activity?.backToPrevious()
        }
        viewBinding.emailSend.setOnClickListener {
            // validate email
            if (!viewBinding.emailInput.text.matches(emailRegex.toRegex())) {
                Toast.makeText(activity, resources.getString(R.string.invalid_email), Toast.LENGTH_SHORT).show()
            } else {
                // TODO: mock up email send success, back to home
                activity?.clearBackStack()
                activity?.replaceFragmentInActivity(HomeFragment(), R.id.container)
            }
        }
        viewBinding.emailInput.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(input: Editable?) {
                if (input == null || input.isEmpty()) {
                    viewBinding.emailSend.visibility = View.INVISIBLE
                    viewBinding.emailSend.isEnabled = false
                } else {
                    viewBinding.emailSend.visibility = View.VISIBLE
                    viewBinding.emailSend.isEnabled = true
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}