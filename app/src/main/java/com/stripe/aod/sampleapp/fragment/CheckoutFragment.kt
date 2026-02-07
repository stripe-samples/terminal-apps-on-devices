package com.example.fridgeapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fridgeapp.R
import com.example.fridgeapp.data.CreatePaymentParams
import com.example.fridgeapp.databinding.FragmentCheckoutBinding
import com.example.fridgeapp.model.CartViewModel
import com.example.fridgeapp.model.CheckoutViewModel
import com.example.fridgeapp.utils.backToHome
import com.example.fridgeapp.utils.formatCentsToString
import com.example.fridgeapp.utils.launchAndRepeatWithViewLifecycle
import com.example.fridgeapp.utils.setThrottleClickListener
import com.stripe.stripeterminal.external.models.PaymentIntentStatus

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {
    private val args: CheckoutFragmentArgs by navArgs()
    private val checkoutViewModel by viewModels<CheckoutViewModel>()
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCheckoutBinding.bind(view)
        val amount = args.amount

        binding.checkoutAmount.text = formatCentsToString(amount)
        binding.cancelButton.visibility = View.VISIBLE

        binding.cancelButton.setThrottleClickListener { findNavController().navigateUp() }

        binding.doneButton.setThrottleClickListener {
            cartViewModel.clearCart()
            backToHome()
        }

        launchAndRepeatWithViewLifecycle {
            checkoutViewModel.currentPaymentIntent.collect { paymentIntent ->
                paymentIntent?.let {
                    when (it.status) {
                        PaymentIntentStatus.REQUIRES_CAPTURE -> {
                            binding.processingIndicator.visibility = View.GONE
                            binding.statusMessage.text = getString(R.string.payment_successful)
                            binding.statusDetail.text = getString(R.string.payment_approved)
                            binding.cancelButton.visibility = View.GONE
                            binding.doneButton.visibility = View.VISIBLE
                        }
                        else -> {
                            /* still processing */
                        }
                    }
                }
            }
        }

        // Build the line items description from the cart
        val cartItems = cartViewModel.cartItems.value
        val description =
                cartItems
                        .joinToString(", ") { item -> "${item.product.name} x${item.quantity}" }
                        .ifEmpty { "Terminal checkout" }

        checkoutViewModel.createPaymentIntent(
                CreatePaymentParams(
                        amount = amount,
                        currency = "usd",
                        description = description,
                )
        ) { failureMessage ->
            binding.processingIndicator.visibility = View.GONE
            binding.statusMessage.text = getString(R.string.payment_failed)
            binding.statusDetail.text =
                    failureMessage.value.ifEmpty {
                        getString(R.string.error_fail_to_create_payment_intent)
                    }
            binding.cancelButton.visibility = View.VISIBLE
            binding.cancelButton.text = getString(R.string.try_again)
        }
    }
}
