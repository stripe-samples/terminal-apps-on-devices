package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.adapter.CartAdapter
import com.stripe.aod.sampleapp.databinding.FragmentCartBinding
import com.stripe.aod.sampleapp.model.CartViewModel
import com.stripe.aod.sampleapp.utils.formatCentsToString
import com.stripe.aod.sampleapp.utils.launchAndRepeatWithViewLifecycle
import com.stripe.aod.sampleapp.utils.navOptions
import com.stripe.aod.sampleapp.utils.setThrottleClickListener

class CartFragment : Fragment(R.layout.fragment_cart) {
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCartBinding.bind(view)

        val adapter = CartAdapter(
            onQuantityChanged = { productId, quantity ->
                cartViewModel.updateQuantity(productId, quantity)
            },
            onRemove = { productId ->
                cartViewModel.removeProduct(productId)
            }
        )

        binding.cartList.layoutManager = LinearLayoutManager(requireContext())
        binding.cartList.adapter = adapter

        binding.back.setThrottleClickListener {
            findNavController().navigateUp()
        }

        binding.clearCart.setThrottleClickListener {
            cartViewModel.clearCart()
        }

        binding.checkoutButton.setThrottleClickListener {
            val total = cartViewModel.cartTotal.value
            if (total > 0) {
                findNavController().navigate(
                    CartFragmentDirections.actionCartFragmentToCheckoutFragment(
                        amount = total.toInt()
                    ),
                    navOptions()
                )
            }
        }

        launchAndRepeatWithViewLifecycle {
            cartViewModel.cartItems.collect { items ->
                adapter.submitList(items.toList())
                binding.emptyCartMessage.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                binding.cartList.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
                binding.checkoutButton.isEnabled = items.isNotEmpty()
            }
        }

        launchAndRepeatWithViewLifecycle {
            cartViewModel.cartTotal.collect { total ->
                binding.totalAmount.text = formatCentsToString(total.toInt())
            }
        }
    }
}
