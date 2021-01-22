package com.yadoms.yadroid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.widgets.NumericViewHolder
import com.yadoms.yadroid.widgets.SwitchViewHolder
import com.yadoms.yadroid.widgets.WidgetViewHolder


class WidgetsRecyclerViewAdapter(
    private val widgets: List<Preferences.Widget>
) : RecyclerView.Adapter<WidgetViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return widgets[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val widgetTypeItem = WidgetTypes.item(WidgetTypes.WidgetType.values()[viewType])!!

        val view = LayoutInflater.from(parent.context)
            .inflate(widgetTypeItem.layout, parent, false)

        return widgetTypeItem.createViewHolder(view)
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.onBind(widgets[position])
    }

    override fun getItemCount(): Int = widgets.size
}