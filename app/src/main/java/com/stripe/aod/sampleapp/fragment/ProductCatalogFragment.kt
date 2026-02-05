package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.adapter.ProductAdapter
import com.stripe.aod.sampleapp.databinding.FragmentProductCatalogBinding
import com.stripe.aod.sampleapp.model.CartViewModel
import com.stripe.aod.sampleapp.model.ProductViewModel
import com.stripe.aod.sampleapp.utils.formatCentsToString
import com.stripe.aod.sampleapp.utils.launchAndRepeatWithViewLifecycle
import com.stripe.aod.sampleapp.utils.navOptions
import com.stripe.aod.sampleapp.utils.setThrottleClickListener

class ProductCatalogFragment : Fragment(R.layout.fragment_product_catalog) {
    private val productViewModel by viewModels<ProductViewModel>()
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProductCatalogBinding.bind(view)

        val adapter = ProductAdapter { product ->
            cartViewModel.addProduct(product)
            Snackbar.make(
                binding.root,
                getString(R.string.added_to_cart, product.name),
                Snackbar.LENGTH_SHORT
            ).show()
        }

        binding.productList.layoutManager = LinearLayoutManager(requireContext())
        binding.productList.adapter = adapter

        binding.back.setThrottleClickListener {
            findNavController().navigateUp()
        }

        binding.viewCartButton.setThrottleClickListener {
            findNavController().navigate(
                R.id.action_productCatalogFragment_to_cartFragment,
                null,
                navOptions()
            )
        }

        binding.swipeRefresh.setOnRefreshListener {
            productViewModel.loadProducts()
        }

        launchAndRepeatWithViewLifecycle {
            productViewModel.products.collect { products ->
                adapter.submitList(products)
                binding.emptyMessage.visibility = if (products.isEmpty() && !productViewModel.isLoading.value) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }

        launchAndRepeatWithViewLifecycle {
            productViewModel.isLoading.collect { isLoading ->
                binding.swipeRefresh.isRefreshing = isLoading
                binding.loadingIndicator.visibility = if (isLoading && productViewModel.products.value.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }

        launchAndRepeatWithViewLifecycle {
            productViewModel.errorMessage.collect { error ->
                error?.let {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                }
            }
        }

        launchAndRepeatWithViewLifecycle {
            cartViewModel.cartItemCount.collect { count ->
                if (count > 0) {
                    binding.cartBar.visibility = View.VISIBLE
                    val total = cartViewModel.cartTotal.value
                    binding.cartSummary.text = getString(
                        R.string.cart_summary,
                        count,
                        formatCentsToString(total.toInt())
                    )
                } else {
                    binding.cartBar.visibility = View.GONE
                }
            }
        }

        productViewModel.loadProducts()
    }
}
