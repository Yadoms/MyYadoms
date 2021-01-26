package com.yadoms.yadroid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.widgets.WidgetViewHolder


//TODO bannir les !! (partout)
//TODO mettre firebase

class WidgetsRecyclerViewAdapter(val preferences: Preferences) : RecyclerView.Adapter<WidgetViewHolder>() {
    private var widgets = preferences.widgets

    private lateinit var view: ViewGroup

    private lateinit var recentlyDeletedWidget: Preferences.Widget
    private var recentlyDeletedWidgetPosition = 0

    override fun getItemViewType(position: Int): Int {
        return widgets[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        view = parent
        val widgetTypeItem = WidgetTypes.item(WidgetTypes.WidgetType.values()[viewType])!!

        val view = LayoutInflater.from(parent.context)
            .inflate(widgetTypeItem.layout, parent, false)

        return widgetTypeItem.createViewHolder(view)
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
        holder.onBind(widgets[position])
    }

    override fun getItemCount(): Int = widgets.size

    fun addNewWidget(widget: Preferences.Widget) {
        widgets.add(widget)
        preferences.saveWidgets(widgets)
        notifyItemInserted(widgets.size - 1)
    }

    fun deleteWidget(position: Int) {
        recentlyDeletedWidget = widgets[position];
        recentlyDeletedWidgetPosition = position;

        widgets.removeAt(position)
        preferences.saveWidgets(widgets)
        notifyItemRemoved(position)
        showUndoSnackbar();
    }

    fun moveWidget(fromPosition: Int, toPosition: Int) {
        val movedWidget = widgets[fromPosition]
        widgets.removeAt(fromPosition)
        widgets.add(if (toPosition > fromPosition + 1) toPosition - 1 else toPosition, movedWidget)
        preferences.saveWidgets(widgets)
        notifyItemMoved(fromPosition, toPosition);
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
    }
}