package com.stripe.aod.sampleapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentHomeBinding
import com.stripe.aod.sampleapp.model.MainViewModel
import com.stripe.aod.sampleapp.utils.launchAndRepeatWithViewLifecycle
import com.stripe.aod.sampleapp.utils.navOptions
import com.stripe.aod.sampleapp.utils.setThrottleClickListener
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.external.models.PaymentStatus
import kotlinx.coroutines.flow.filter

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewBinding = FragmentHomeBinding.bind(view)

        viewBinding.menuSettings.setThrottleClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("stripe://settings/")))
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.readerConnectStatus.collect {
                viewBinding.indicator.visibility = if (it != ConnectionStatus.CONNECTED) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.readerPaymentStatus.collect {
                viewBinding.newPayment.isEnabled = (it == PaymentStatus.READY)
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.userMessage.filter {
                it.isNotEmpty()
            }.collect { message ->
                Snackbar.make(viewBinding.newPayment, message, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewBinding.newPayment.setThrottleClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_inputFragment,
                null,
                navOptions()
            )
        }
    }
}
