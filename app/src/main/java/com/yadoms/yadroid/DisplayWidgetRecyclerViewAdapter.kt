package com.yadoms.yadroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.yadroid.preferences.Preferences

class DisplayWidgetRecyclerViewAdapter(
    private val widgets: List<Preferences.Widget>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<DisplayWidgetRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.display_widget_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = widgets[position]
        holder.buttonView.text = item.name
        holder.nameView.text = item.name + item.keywordId.toString()
        holder.valueView.text = "0.0"
    }

    override fun getItemCount(): Int = widgets.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val buttonView: Button = view.findViewById(R.id.button)
        val nameView: TextView = view.findViewById(R.id.name)
        val valueView: TextView = view.findViewById(R.id.value)

        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION)
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