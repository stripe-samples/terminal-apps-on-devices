package com.stripe.aod.sampleapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentHomeBinding
import com.stripe.aod.sampleapp.utils.navOptions

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewBinding = FragmentHomeBinding.bind(view)

        viewBinding.menuSettings.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("stripe://settings/")))
        }

        viewBinding.menuConfig.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_configFragment,
                null,
                navOptions()
            )
        }
    }
}
