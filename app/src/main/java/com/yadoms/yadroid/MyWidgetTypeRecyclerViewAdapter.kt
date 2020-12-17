package com.yadoms.yadroid

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION

import com.yadoms.yadroid.dummy.WidgetTypesContent.WidgetTypeItem

/**
 * [RecyclerView.Adapter] that can display a [WidgetTypeItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyWidgetTypeRecyclerViewAdapter(
    private val values: List<WidgetTypeItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MyWidgetTypeRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name
        holder.descriptionView.text = item.description
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val nameView: TextView = view.findViewById(R.id.item_type)
        val descriptionView: TextView = view.findViewById(R.id.item_description)

        override fun toString(): String {
            return super.toString() + " '" + descriptionView.text + "'"
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
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