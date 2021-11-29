package com.yadoms.myyadoms

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.myyadoms.widgets.WidgetTypes
import com.yadoms.myyadoms.widgets.WidgetViewHolder
import com.yadoms.myyadoms.yadomsApi.YadomsApi


class WidgetsRecyclerViewAdapter(val context: Context, private val emptyListener: EmptyListener) : RecyclerView.Adapter<WidgetViewHolder>() {
    private var widgetsModels = WidgetsModels(context)

    override fun getItemViewType(position: Int): Int {
        return widgetsModels.type(position).ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val widgetTypeItem = WidgetTypes.item(WidgetTypes.WidgetType.values()[viewType])!!
        return widgetTypeItem.createViewHolder(LayoutInflater.from(context).inflate(widgetTypeItem.layout, parent, false))
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.onBind(widgetsModels.at(position))
    }

    override fun getItemCount(): Int = widgetsModels.count

    fun addNewWidget(widgetConfiguration: WidgetConfiguration) {
        val position = widgetsModels.create(widgetConfiguration)

        notifyItemInserted(position)

        if (widgetsModels.count == 1)
            emptyListener.onEmptyChange(false)
    }

    fun deleteWidget(position: Int) {
        widgetsModels.delete(position)

        notifyItemRemoved(position)
        if (widgetsModels.empty)
            emptyListener.onEmptyChange(true)
    }

    fun moveWidget(fromPosition: Int, toPosition: Int) {
        widgetsModels.move(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun undoDelete() {
        val restoredPosition = widgetsModels.restore()

        notifyItemInserted(restoredPosition)

        if (widgetsModels.count == 1)
            emptyListener.onEmptyChange(false)
    }
}