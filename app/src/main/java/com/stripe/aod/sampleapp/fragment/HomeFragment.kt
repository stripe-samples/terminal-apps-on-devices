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
import kotlinx.coroutines.flow.filter

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewBinding = FragmentHomeBinding.bind(view)

        viewBinding.menuSettings.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("stripe://settings/")))
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.isReaderConnected.collect {
                viewBinding.newPayment.isEnabled = it
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.userMessage.filter {
                it.isNotEmpty()
            }.collect { message ->
                Snackbar.make(viewBinding.newPayment, message, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewBinding.newPayment.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_inputFragment,
                null,
                navOptions()
            )
        }
    }
}
