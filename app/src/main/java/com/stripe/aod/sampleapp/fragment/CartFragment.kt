package com.example.fridgeapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fridgeapp.R
import com.example.fridgeapp.adapter.CartAdapter
import com.example.fridgeapp.databinding.FragmentCartBinding
import com.example.fridgeapp.model.CartViewModel
import com.example.fridgeapp.utils.formatCentsToString
import com.example.fridgeapp.utils.launchAndRepeatWithViewLifecycle
import com.example.fridgeapp.utils.navOptions
import com.example.fridgeapp.utils.setThrottleClickListener

class CartFragment : Fragment(R.layout.fragment_cart) {
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCartBinding.bind(view)

        val adapter =
                CartAdapter(
                        onQuantityChanged = { productId, quantity ->
                            cartViewModel.updateQuantity(productId, quantity)
                        },
                        onRemove = { productId -> cartViewModel.removeProduct(productId) }
                )

        binding.cartList.layoutManager = LinearLayoutManager(requireContext())
        binding.cartList.adapter = adapter

        binding.back.setThrottleClickListener { findNavController().navigateUp() }

        binding.clearCart.setThrottleClickListener { cartViewModel.clearCart() }

        binding.checkoutButton.setThrottleClickListener {
            val total = cartViewModel.cartTotal.value
            if (total > 0) {
                findNavController()
                        .navigate(
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
                binding.emptyCartMessage.visibility =
                        if (items.isEmpty()) View.VISIBLE else View.GONE
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
