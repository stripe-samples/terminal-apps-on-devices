package com.stripe.aod.sampleapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Callback
import com.stripe.stripeterminal.external.callable.ReaderCallback
import com.stripe.stripeterminal.external.models.ConnectionConfiguration
import com.stripe.stripeterminal.external.models.Reader
import com.stripe.stripeterminal.external.models.TerminalException
import java.lang.ref.WeakReference


class ReaderAdapter(private val activityRef: WeakReference<MainActivity>) : RecyclerView.Adapter<ReaderAdapter.ReaderHolder>() {
    class ReaderHolder(view : View) : RecyclerView.ViewHolder(view){
        val readerTitle: TextView = view.findViewById(R.id.reader_title)
        val readerDesc: TextView = view.findViewById(R.id.reader_desc)
        val readerStatus: TextView = view.findViewById(R.id.reader_status)
        val readerIsOnline: ImageView = view.findViewById(R.id.reader_isOnline)
        val cardView: CardView = view.findViewById(R.id.reader_card)
    }

    private val mDiffer: AsyncListDiffer<Reader> = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReaderHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reader,parent,false)
        return ReaderHolder(view)
    }

    override fun onBindViewHolder(holder: ReaderHolder, position: Int) {
        val reader = mDiffer.currentList[position]
        holder.readerTitle.text = reader.serialNumber?: reader.id ?: activityRef.get()?.resources?.getString(R.string.unknown_reader)
        holder.readerDesc.text =  reader.location?.displayName ?: ""

        //update reader online status
        if (Reader.NetworkStatus.ONLINE == reader.networkStatus) {
            holder.readerIsOnline.setImageResource(R.drawable.ic_online)
        } else {
            holder.readerIsOnline.setImageResource(R.drawable.ic_offline)
        }

        val currentReader: Reader? = Terminal.getInstance().connectedReader
        if (reader.id == currentReader?.id) {
            holder.readerStatus.visibility = View.VISIBLE
            holder.readerStatus.text = activityRef.get()?.resources?.getString(R.string.connected)
        } else {
            holder.readerStatus.visibility = View.GONE
            holder.readerStatus.text = ""
        }

        holder.cardView.setOnClickListener {
            connectReader(reader)
        }
    }

    override fun getItemCount(): Int {
        return mDiffer.currentList.size
    }

    fun updateReaders(readers: List<Reader>) {
        mDiffer.submitList(readers)
    }

    private fun connectReader(reader: Reader) {
        if (Terminal.getInstance().connectedReader != null) {
            //same one , skip
            if (reader.id === Terminal.getInstance().connectedReader!!.id) {
                Toast.makeText(activityRef.get()?.applicationContext, activityRef.get()?.resources?.getString(R.string.status_reader_connected), Toast.LENGTH_SHORT).show()
                return
            }

            //different reader , disconnect old first then connect new one again
            val lastReader: Reader? = Terminal.getInstance().connectedReader
            Terminal.getInstance().disconnectReader(object : Callback {
                override fun onSuccess() {
                    Log.d(Config.TAG, "Last Reader[ " + lastReader?.id + " ] disconnect success ")
                    activityRef.get()?.runOnUiThread {
                        notifyItemChanged(mDiffer.currentList.indexOf(lastReader))
                    }
                }

                override fun onFailure(e: TerminalException) {
                    Log.e(Config.TAG, "Last Reader[ " + lastReader?.id + " ] disconnect fail ")
                }
            })
        }

        if (reader.networkStatus != Reader.NetworkStatus.ONLINE) {
            Toast.makeText(activityRef.get()?.applicationContext, activityRef.get()?.resources?.getString(R.string.status_reader_offline), Toast.LENGTH_SHORT).show()
            return
        }

        Log.i(Config.TAG, "Connecting new Reader[ " + reader.id + " ] .... ")

        val readerCallback: ReaderCallback = object : ReaderCallback {
            override fun onSuccess(reader: Reader) {
                activityRef.get()?.runOnUiThread {
                    notifyItemChanged(mDiffer.currentList.indexOf(reader))
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
    companion object {
        val diffCallback: DiffUtil.ItemCallback<Reader> = object : DiffUtil.ItemCallback<Reader>() {
            override fun areItemsTheSame(oldItem: Reader, newItem: Reader): Boolean {
                return oldItem.id == (newItem.id)
            }

            override fun areContentsTheSame(oldItem: Reader, newItem: Reader): Boolean {
                return oldItem == newItem
            }
        }
    }
}