package com.yadoms.yadroid

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.yadoms.yadroid.yadomsApi.DeviceApi

class SelectDeviceRecyclerViewAdapter(
    private val values: List<DeviceApi.Device>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SelectDeviceRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_device_fragment_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.friendlyName
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val nameView: TextView = view.findViewById(R.id.item_device_name)

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }

        override fun onClick(p0: View?) {
            val position = bindingAdapterPosition
            if (position != NO_POSITION)
                listener.onItemClick(position)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}