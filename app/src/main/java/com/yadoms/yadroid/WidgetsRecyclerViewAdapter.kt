package com.yadoms.yadroid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.widgets.WidgetViewHolder

//TODO bannir les !!

class WidgetsRecyclerViewAdapter(val preferences: Preferences) : RecyclerView.Adapter<WidgetViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return preferences.widgets[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val widgetTypeItem = WidgetTypes.item(WidgetTypes.WidgetType.values()[viewType])!!

        val view = LayoutInflater.from(parent.context)
            .inflate(widgetTypeItem.layout, parent, false)

        return widgetTypeItem.createViewHolder(view)
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.onBind(preferences.widgets[position])
    }

    override fun getItemCount(): Int = preferences.widgets.size

    fun deleteItem(position: Int) {
        preferences.removeWidget(position)
    }
}