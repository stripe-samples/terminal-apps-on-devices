package com.stripe.aod.sampleapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.ItemReaderBinding
import com.stripe.aod.sampleapp.utils.toast
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.callable.ReaderCallback
import com.stripe.stripeterminal.external.models.ConnectionConfiguration
import com.stripe.stripeterminal.external.models.Reader
import com.stripe.stripeterminal.external.models.TerminalException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ReaderAdapter : RecyclerView.Adapter<ReaderAdapter.ReaderHolder>() {
    private companion object {
        val diffCallback: DiffUtil.ItemCallback<Reader> = object : DiffUtil.ItemCallback<Reader>() {
            override fun areItemsTheSame(oldItem: Reader, newItem: Reader): Boolean {
                return oldItem.id == (newItem.id)
            }

            override fun areContentsTheSame(oldItem: Reader, newItem: Reader): Boolean {
                return oldItem == newItem
            }
        }
    }

    private val differ: AsyncListDiffer<Reader> = AsyncListDiffer(this, diffCallback)
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReaderHolder {
        context = parent.context
        val binding: ItemReaderBinding = ItemReaderBinding.inflate(LayoutInflater.from(context), parent, false)
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

    private fun connectReader(reader: Reader) {
        if (Terminal.getInstance().connectedReader != null) {
            //same one , skip
            if (reader.id === Terminal.getInstance().connectedReader!!.id) {
                context.toast(R.string.status_reader_connected)
                return
            }

            //different reader , disconnect old first then connect new one again
            val lastReader: Reader? = Terminal.getInstance().connectedReader
            Terminal.getInstance().disconnectReader(object : Callback {
                override fun onSuccess() {
                    Log.d(Config.TAG, "Last Reader [${lastReader?.id}] disconnect success ")
                    MainScope().launch {
                        notifyItemChanged(differ.currentList.indexOf(lastReader))
                    }
                }

                override fun onFailure(e: TerminalException) {
                    Log.e(Config.TAG, "Last Reader [${lastReader?.id}] disconnect fail ")
                }
            })
        }

        if (reader.networkStatus != Reader.NetworkStatus.ONLINE) {
            context.toast(R.string.status_reader_offline)
            return
        }

        Log.i(Config.TAG, "Connecting new Reader [${reader.id}] .... ")

        val readerCallback: ReaderCallback = object : ReaderCallback {
            override fun onSuccess(reader: Reader) {
                MainScope().launch {
                    notifyItemChanged(differ.currentList.indexOf(reader))
                }
            }

            override fun onFailure(e: TerminalException) {
            }
        }

        Terminal.getInstance().connectHandoffReader(
            reader,
            ConnectionConfiguration.HandoffConnectionConfiguration(),
            null,
            readerCallback)
    }

    inner class ReaderHolder(private val binding: ItemReaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reader: Reader) {
            binding.apply {
                readerTitle.text = reader.serialNumber?: reader.id ?: context.resources.getString(R.string.unknown_reader)
                readerDesc.text =  reader.location?.displayName ?: ""

                //update reader online status
                if (Reader.NetworkStatus.ONLINE == reader.networkStatus) {
                    readerIsOnline.setImageResource(R.drawable.ic_online)
                } else {
                    readerIsOnline.setImageResource(R.drawable.ic_offline)
                }

                val currentReader: Reader? = Terminal.getInstance().connectedReader
                if (reader.id == currentReader?.id) {
                    readerStatus.visibility = View.VISIBLE
                    readerStatus.text = context.resources.getString(R.string.connected)
                } else {
                    readerStatus.visibility = View.GONE
                    readerStatus.text = ""
                }

                readerCard.setOnClickListener {
                    connectReader(reader)
                }
            }
        }
    }
}