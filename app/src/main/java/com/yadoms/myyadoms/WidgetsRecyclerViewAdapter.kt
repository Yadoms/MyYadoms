package com.yadoms.myyadoms

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.myyadoms.preferences.Preferences
import com.yadoms.myyadoms.widgets.WidgetTypes
import com.yadoms.myyadoms.widgets.WidgetViewHolder
import com.yadoms.myyadoms.yadomsApi.DeviceApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi


class WidgetsRecyclerViewAdapter(val context: Context, private val emptyListener: EmptyListener) : RecyclerView.Adapter<WidgetViewHolder>() {
    private var widgetsModels = createModels()
    private val _logTag = javaClass.canonicalName

    private fun createModels(): MutableList<Preferences.WidgetModel> {
        val widgetModels: MutableList<Preferences.WidgetModel> = mutableListOf()
        Preferences(context).widgets.map { widgetData ->
            val item = WidgetTypes.item(widgetData.type)
            item?.let { widgetModels.add(it.createModel(widgetData)) }
        }
        return widgetModels
    }

    private fun saveWidgets() {
        val widgetsData: MutableList<Preferences.WidgetData> = mutableListOf()
        widgetsModels.map { widgetModel ->
            widgetsData.add(widgetModel.data)
        }
        Preferences(context).saveWidgets(widgetsData)
    }

    private lateinit var recentlyDeletedWidget: Preferences.WidgetModel
    private var recentlyDeletedWidgetPosition = 0

    override fun getItemViewType(position: Int): Int {
        return widgetsModels[position].data.type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetViewHolder {
        val widgetTypeItem = WidgetTypes.item(WidgetTypes.WidgetType.entries[viewType])!!
        return widgetTypeItem.createViewHolder(LayoutInflater.from(context).inflate(widgetTypeItem.layout, parent, false))
    }

    override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
//TODO virer (et virer le Model ?) ?        widgetsModels[position].requestState()
        holder.onBind(widgetsModels[position])
    }

    override fun getItemCount(): Int = widgetsModels.size

    fun addNewWidget(widgetData: Preferences.WidgetData) {
        val item = WidgetTypes.item(widgetData.type)
        item?.let { widgetsModels.add(it.createModel(widgetData)) }

        saveWidgets()

        notifyItemInserted(widgetsModels.size - 1)

        if (widgetsModels.size == 1)
            emptyListener.onEmptyChange(false)
    }

    fun deleteWidget(position: Int) {
        recentlyDeletedWidget = widgetsModels[position]
        recentlyDeletedWidgetPosition = position

        widgetsModels.removeAt(position)
        saveWidgets()
        notifyItemRemoved(position)

        if (widgetsModels.isEmpty())
            emptyListener.onEmptyChange(true)
    }

    fun moveWidget(fromPosition: Int, toPosition: Int) {
        val movedWidget = widgetsModels[fromPosition]
        widgetsModels.removeAt(fromPosition)
        widgetsModels.add(if (toPosition > fromPosition + 1) toPosition - 1 else toPosition, movedWidget)
        saveWidgets()
        notifyItemMoved(fromPosition, toPosition)
    }

    fun undoDelete() {
        widgetsModels.add(
            recentlyDeletedWidgetPosition,
            recentlyDeletedWidget
        )
        saveWidgets()
        notifyItemInserted(recentlyDeletedWidgetPosition)

        if (widgetsModels.size == 1)
            emptyListener.onEmptyChange(false)
    }


    fun refreshAllWidgets() {
        // TODO(Yadoms APIv2) à optimiser en API v2 pour ne faire qu'une requête
        // TODO optimiser pour ne raffraichir que les éléments visibles
        widgetsModels.forEach { widgetModel ->
            DeviceApi(YadomsApi(context)).getKeyword(widgetModel.data.keywordId,
                onOk = {
                    Log.d(_logTag, "keyword ${widgetModel.data.keywordId}) = ${it.lastAcquisitionValue} at ${it.lastAcquisitionDate}")
                    if (widgetModel.lastState != it) {
                        widgetModel.lastState = it
                        notifyItemChanged(widgetsModels.indexOf(widgetModel))
                    }
                }
            ) { error ->
                Log.e(_logTag, "keyword ${widgetModel.data.keywordId}) = error get last acquisition : ${error}")
                if (widgetModel.lastState != null) {
                    widgetModel.lastState = null
                    notifyItemChanged(widgetsModels.indexOf(widgetModel))
                }
            }
        }
    }
}