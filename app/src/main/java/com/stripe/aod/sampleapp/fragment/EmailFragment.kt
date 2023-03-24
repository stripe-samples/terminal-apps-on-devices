package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.utils.backToPrevious
import com.stripe.aod.sampleapp.utils.clearBackStack
import com.stripe.aod.sampleapp.utils.replaceFragmentInActivity
import kotlinx.android.synthetic.main.fragment_email.*

class EmailFragment: Fragment(R.layout.fragment_email),View.OnClickListener {

    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.EmailFragment"
    }

    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rl_back.setOnClickListener(this)
        email_send.setOnClickListener(this)
        email_input.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(input: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(input: Editable?) {
                if (input == null || input.isEmpty()) {
                    email_send.visibility = View.INVISIBLE
                    email_send.isEnabled = false
                } else {
                    email_send.visibility = View.VISIBLE
                    email_send.isEnabled = true
                }
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rl_back -> {
                activity?.backToPrevious()
            }

            R.id.email_send -> {
                // validate email
                if (!email_input.text.matches(emailRegex.toRegex())) {
                    Toast.makeText(activity, resources.getString(R.string.invalid_email), Toast.LENGTH_SHORT).show()
                    return
                }

                // TODO: mock up email send success, back to home
                activity?.clearBackStack()
                activity?.replaceFragmentInActivity(HomeFragment(),R.id.container)
            }
        }
    }
}