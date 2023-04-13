package com.stripe.aod.sampleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.adapter.data.ReaderListItem
import com.stripe.aod.sampleapp.databinding.ItemReaderBinding
import com.stripe.aod.sampleapp.model.DiscoveryViewModel
import com.stripe.stripeterminal.external.models.Reader

class ReaderAdapter(val discoveryViewModel: DiscoveryViewModel) : RecyclerView.Adapter<ReaderAdapter.ReaderHolder>() {
    private companion object {
        val diffCallback: DiffUtil.ItemCallback<ReaderListItem> = object : DiffUtil.ItemCallback<ReaderListItem>() {
            override fun areItemsTheSame(oldItem: ReaderListItem, newItem: ReaderListItem): Boolean {
                return (oldItem.reader.id == newItem.reader.id) && (oldItem.isConnected == newItem.isConnected)
            }

            override fun areContentsTheSame(oldItem: ReaderListItem, newItem: ReaderListItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    private val differ: AsyncListDiffer<ReaderListItem> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReaderHolder {
        val binding: ItemReaderBinding = ItemReaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReaderHolder(binding)
    }

    override fun onBindViewHolder(holder: ReaderHolder, position: Int) {
        val readerListItem = differ.currentList[position]
        holder.bind(readerListItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun updateReaders(readers: List<ReaderListItem>) {
        differ.submitList(readers)
    }

    inner class ReaderHolder(private val binding: ItemReaderBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(readerListItem: ReaderListItem) {
            binding.apply {
                val resources = root.context.resources
                readerTitle.text = readerListItem.reader.serialNumber ?: readerListItem.reader.id ?: resources.getString(
                    R.string.unknown_reader
                )
                readerDesc.text = readerListItem.reader.location?.displayName ?: ""

                // update reader online status
                if (Reader.NetworkStatus.ONLINE == readerListItem.reader.networkStatus) {
                    readerIsOnline.setImageResource(R.drawable.ic_online)
                } else {
                    readerIsOnline.setImageResource(R.drawable.ic_offline)
                }

                val currentReader: Reader? = discoveryViewModel.getCurrentReader()
                if ((readerListItem.reader.id == currentReader?.id) && readerListItem.isConnected) {
                    readerStatus.visibility = View.VISIBLE
                    readerStatus.text = resources.getString(R.string.connected)
                } else {
                    readerStatus.visibility = View.GONE
                    readerStatus.text = ""
                }

                readerCard.setOnClickListener {
                    discoveryViewModel.connectReader(root.context, readerListItem.reader)
                }
            }
        }
    }
}
