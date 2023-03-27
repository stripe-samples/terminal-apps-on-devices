package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.databinding.FragmentConfigBinding
import com.stripe.aod.sampleapp.utils.backToPrevious
import com.stripe.aod.sampleapp.utils.navigateToTarget

class ConfigFragment : Fragment(R.layout.fragment_config) {
    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.ConfigFragment"
    }

    private var _viewBinding : FragmentConfigBinding? = null
    private val viewBinding get() = _viewBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentConfigBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.discoverButton.setOnClickListener {
            activity?.navigateToTarget(
                DiscoverReaderFragment.TAG,
                DiscoverReaderFragment(),
                true,
                true
            )
        }

        viewBinding.rlBack.setOnClickListener {
            activity?.backToPrevious()
        }

        //hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(activity as MainActivity, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity?.backToPrevious()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}