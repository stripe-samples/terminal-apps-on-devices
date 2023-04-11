package com.stripe.aod.sampleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.ItemReaderBinding
import com.stripe.aod.sampleapp.model.DiscoveryViewModel
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.models.Reader

class ReaderAdapter(val discoveryViewModel: DiscoveryViewModel) : RecyclerView.Adapter<ReaderAdapter.ReaderHolder>() {
    private companion object {
        val diffCallback: DiffUtil.ItemCallback<Reader> = object : DiffUtil.ItemCallback<Reader>() {
            override fun areItemsTheSame(oldItem: Reader, newItem: Reader): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Reader, newItem: Reader): Boolean {
                return oldItem == newItem
            }
        }
    }

    private val differ: AsyncListDiffer<Reader> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReaderHolder {
        val binding: ItemReaderBinding = ItemReaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReaderHolder(binding)
    }

    override fun onBindViewHolder(holder: ReaderHolder, position: Int) {
        val reader = differ.currentList[position]
        holder.bind(reader)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun updateReaders(readers: List<Reader>) {
        differ.submitList(readers)
    }

    /**
     *  Just not care about performance here since we'll should only have a handful of readers (most likely just one)
     */
    fun refreshUI() {
        notifyDataSetChanged()
    }

    inner class ReaderHolder(private val binding: ItemReaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reader: Reader) {
            binding.apply {
                val resources = root.context.resources
                readerTitle.text = reader.serialNumber ?: reader.id ?: resources.getString(R.string.unknown_reader)
                readerDesc.text = reader.location?.displayName ?: ""

                // update reader online status
                if (Reader.NetworkStatus.ONLINE == reader.networkStatus) {
                    readerIsOnline.setImageResource(R.drawable.ic_online)
                } else {
                    readerIsOnline.setImageResource(R.drawable.ic_offline)
                }

                val currentReader: Reader? = Terminal.getInstance().connectedReader
                if (reader.id == currentReader?.id) {
                    readerStatus.visibility = View.VISIBLE
                    readerStatus.text = resources.getString(R.string.connected)
                } else {
                    readerStatus.visibility = View.GONE
                    readerStatus.text = ""
                }

                readerCard.setOnClickListener {
                    discoveryViewModel.connectReader(root.context, reader)
                }
            }
        }
    }
}
