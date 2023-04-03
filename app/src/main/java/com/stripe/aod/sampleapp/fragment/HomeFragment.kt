package com.stripe.aod.sampleapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.HomeFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewBinding = FragmentHomeBinding.bind(view)

        viewBinding.menuSettings.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("stripe://settings/")))
        }
    }
}
