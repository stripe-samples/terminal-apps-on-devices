package com.stripe.aod.sampleapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentHomeBinding
import com.stripe.aod.sampleapp.utils.navigateToTarget
import com.stripe.aod.sampleapp.utils.toast
import com.stripe.stripeterminal.Terminal

class HomeFragment : Fragment(R.layout.fragment_home) {
    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.HomeFragment"
    }

    private var _viewBinding : FragmentHomeBinding? = null
    private val viewBinding get() = _viewBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _viewBinding = FragmentHomeBinding.bind(view)

        viewBinding.ivSettings.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("stripe://settings/")))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewBinding.btnNewPayment.setOnClickListener {
            if (Terminal.getInstance().connectedReader == null ) {
                toast(getString(R.string.please_select_reader))
            } else {
                activity?.navigateToTarget(InputFragment.TAG, InputFragment(),
                    replace = true,
                    addToBackStack = true
                )
            }
        }

        viewBinding.ivConfig.setOnClickListener {
            activity?.navigateToTarget(ConfigFragment.TAG, ConfigFragment(),
                replace = true,
                addToBackStack = true
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}