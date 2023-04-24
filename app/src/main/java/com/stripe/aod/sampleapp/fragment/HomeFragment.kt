package com.stripe.aod.sampleapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentHomeBinding
import com.stripe.aod.sampleapp.model.MainViewModel
import com.stripe.aod.sampleapp.utils.launchAndRepeatWithViewLifecycle
import kotlinx.coroutines.flow.filter

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewBinding = FragmentHomeBinding.bind(view)

        viewBinding.menuSettings.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("stripe://settings/")))
        }

        val viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
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
    }
}
