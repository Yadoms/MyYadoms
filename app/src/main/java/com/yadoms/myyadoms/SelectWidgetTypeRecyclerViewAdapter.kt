package com.yadoms.myyadoms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.yadoms.myyadoms.widgets.WidgetTypes.WidgetTypeItem

/**
 * [RecyclerView.Adapter] that can display a [WidgetTypeItem].
 */
class SelectWidgetTypeRecyclerViewAdapter(
    private val values: List<WidgetTypeItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<SelectWidgetTypeRecyclerViewAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.select_widget_fragment_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.logoView.setImageResource(item.logo)
        holder.nameView.text = context.getString(item.nameRessourceId)
        holder.descriptionView.text = context.getString(item.descriptionRessourceId)
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val logoView: ImageView = view.findViewById(R.id.item_logo)
        val nameView: TextView = view.findViewById(R.id.item_type)
        val descriptionView: TextView = view.findViewById(R.id.item_description)

        override fun toString(): String {
            return super.toString() + " '" + descriptionView.text + "'"
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