package com.yadoms.yadroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.yadroid.preferences.Preferences


class WidgetSwitchRecyclerViewAdapter(
    private val widgets: List<Preferences.Widget>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<WidgetSwitchRecyclerViewAdapter.WidgetViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return widgets[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val layout = when (WidgetTypes.WidgetType.values()[viewType]) {
            WidgetTypes.WidgetType.Switch -> R.layout.widget_switch_item
            WidgetTypes.WidgetType.Numeric -> R.layout.widget_numeric_item
        }

        val view = LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)

        return when (WidgetTypes.WidgetType.values()[viewType]) {
            WidgetTypes.WidgetType.Switch -> SwitchViewHolder(view)
            WidgetTypes.WidgetType.Numeric -> NumericViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.onBind(widgets[position])
    }

    override fun getItemCount(): Int = widgets.size

    abstract inner class WidgetViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        abstract val name: String

        override fun toString(): String {
            return super.toString() + " '" + name + "'"
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION)
                listener.onItemClick(position)
        }

        abstract fun onBind(widget: Preferences.Widget)

        init {
            itemView.setOnClickListener(this)
        }
    }

    inner class SwitchViewHolder(view: View) : WidgetViewHolder(view) {
        val buttonView: Button = view.findViewById(R.id.button)
        val nameView: TextView = view.findViewById(R.id.name)
        val valueView: TextView = view.findViewById(R.id.value)
        override val name: String
            get() = "Switch"

        override fun onBind(widget: Preferences.Widget) {
            buttonView.text = widget.name
            nameView.text = widget.name + widget.keywordId.toString()
            valueView.text = "0.0"
        }
    }

    inner class NumericViewHolder(view: View) : WidgetViewHolder(view) {
        val buttonView: Button = view.findViewById(R.id.button)
        val nameView: TextView = view.findViewById(R.id.name)
        override val name: String
            get() = "Numeric"

        override fun onBind(widget: Preferences.Widget) {
            buttonView.text = widget.name
            nameView.text = widget.name + widget.keywordId.toString()
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}