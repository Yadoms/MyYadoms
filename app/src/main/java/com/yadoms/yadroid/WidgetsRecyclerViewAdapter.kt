package com.yadoms.yadroid

import Preferences
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yadoms.yadroid.widgets.WidgetTypes
import com.yadoms.yadroid.widgets.WidgetViewHolder


class WidgetsRecyclerViewAdapter(val context: Context?) : RecyclerView.Adapter<WidgetViewHolder>() {
    var items: List<Preferences.Widget> = listOf()
        set(value) {
            val oldList = field
            field = value
            refreshList(value, oldList)
        }

    private lateinit var recentlyDeletedWidget: Preferences.WidgetData
    private var recentlyDeletedWidgetPosition = 0

    override fun getItemViewType(position: Int): Int {
        return items[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val widgetTypeItem = WidgetTypes.item(WidgetTypes.WidgetType.values()[viewType])!!
        return widgetTypeItem.createViewHolder(LayoutInflater.from(context).inflate(widgetTypeItem.layout, parent, false))
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        ###
        widgets[position].requestState()
        holder.onBind(widgets[position])
    }

    override fun getItemCount(): Int = widgets.size

    fun addNewWidget(widget: Preferences.WidgetData) {
        widgets.add(widget)
        preferences.saveWidgets(widgets)
        notifyItemInserted(widgets.size - 1)

        if (widgets.size == 1)
            emptyListener.onEmptyChange(false)
    }

    fun deleteWidget(position: Int) {
        recentlyDeletedWidget = widgets[position]
        recentlyDeletedWidgetPosition = position

        widgets.removeAt(position)
        preferences.saveWidgets(widgets)
        notifyItemRemoved(position)
        showUndoSnackbar()

        if (widgets.isEmpty())
            emptyListener.onEmptyChange(true)
    }

    fun moveWidget(fromPosition: Int, toPosition: Int) {
        val movedWidget = widgets[fromPosition]
        widgets.removeAt(fromPosition)
        widgets.add(if (toPosition > fromPosition + 1) toPosition - 1 else toPosition, movedWidget)
        preferences.saveWidgets(widgets)
        notifyItemMoved(fromPosition, toPosition)
    }

    private fun showUndoSnackbar() {
        val snackbar: Snackbar = Snackbar.make(
            view, view.context.getString(R.string.widget_deleted),
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction(view.context.getString(R.string.undo)) { undoDelete() }
        snackbar.show()
    }

    private fun undoDelete() {
        widgets.add(
            recentlyDeletedWidgetPosition,
            recentlyDeletedWidget
        )
        preferences.saveWidgets(widgets)
        notifyItemInserted(recentlyDeletedWidgetPosition)

        if (widgets.size == 1)
            emptyListener.onEmptyChange(false)
    }
}