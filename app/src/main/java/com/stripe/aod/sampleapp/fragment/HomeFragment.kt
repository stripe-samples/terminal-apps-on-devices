package com.example.fridgeapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fridgeapp.R
import com.example.fridgeapp.databinding.FragmentHomeBinding
import com.example.fridgeapp.model.MainViewModel
import com.example.fridgeapp.utils.launchAndRepeatWithViewLifecycle
import com.example.fridgeapp.utils.navOptions
import com.example.fridgeapp.utils.setThrottleClickListener
import com.google.android.material.snackbar.Snackbar
import com.stripe.stripeterminal.external.models.ConnectionStatus
import com.stripe.stripeterminal.external.models.PaymentStatus
import kotlinx.coroutines.flow.filter

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewBinding = FragmentHomeBinding.bind(view)

        viewBinding.menuSettings.setThrottleClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("stripe://settings/")))
            } catch (e: android.content.ActivityNotFoundException) {
                Snackbar.make(viewBinding.menuSettings, "Stripe settings not available on this device", Snackbar.LENGTH_SHORT).show()
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.readerConnectStatus.collect {
                viewBinding.indicator.visibility =
                        if (it != ConnectionStatus.CONNECTED) {
                            View.VISIBLE
                        } else {
                            View.INVISIBLE
                        }
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.readerPaymentStatus.collect {
                val isReady = it == PaymentStatus.READY
                viewBinding.newPayment.isEnabled = isReady
                viewBinding.browseProducts.isEnabled = isReady
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.userMessage.filter { it.isNotEmpty() }.collect { message ->
                Snackbar.make(viewBinding.newPayment, message, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewBinding.browseProducts.setThrottleClickListener {
            findNavController()
                    .navigate(
                            R.id.action_homeFragment_to_productCatalogFragment,
                            null,
                            navOptions()
                    )
        }

        viewBinding.newPayment.setThrottleClickListener {
            findNavController()
                    .navigate(R.id.action_homeFragment_to_inputFragment, null, navOptions())
        }
    }
}
