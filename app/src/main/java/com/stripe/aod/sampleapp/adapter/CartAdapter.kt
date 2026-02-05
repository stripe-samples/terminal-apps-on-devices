package com.stripe.aod.sampleapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stripe.aod.sampleapp.data.CartItem
import com.stripe.aod.sampleapp.databinding.ItemCartBinding
import com.stripe.aod.sampleapp.utils.formatCentsToString
import com.stripe.aod.sampleapp.utils.setThrottleClickListener

class CartAdapter(
    private val onQuantityChanged: (String, Int) -> Unit,
    private val onRemove: (String) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.itemName.text = cartItem.product.name
            binding.itemUnitPrice.text = formatCentsToString(cartItem.product.unitAmount.toInt())
            binding.itemQuantity.text = cartItem.quantity.toString()
            binding.itemLineTotal.text = formatCentsToString(cartItem.lineTotal.toInt())

            binding.btnDecrease.setThrottleClickListener(intervalDuration = 300) {
                onQuantityChanged(cartItem.product.id, cartItem.quantity - 1)
            }

            binding.btnIncrease.setThrottleClickListener(intervalDuration = 300) {
                onQuantityChanged(cartItem.product.id, cartItem.quantity + 1)
            }

            binding.btnRemove.setThrottleClickListener {
                onRemove(cartItem.product.id)
            }
        }
    }

    private class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
