package com.yadoms.myyadoms

import android.content.Context
import com.yadoms.myyadoms.preferences.Preferences
import com.yadoms.myyadoms.widgets.WidgetModel
import com.yadoms.myyadoms.widgets.WidgetTypes
import com.yadoms.myyadoms.yadomsApi.DeviceApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi


class WidgetsModels(val context: Context) {
    private val deviceApi = DeviceApi(YadomsApi(context))
    var models: MutableList<WidgetModel> = load()

    private data class RecentlyDeletedWidget(val position: Int, val configuration: WidgetConfiguration)

    private var recentlyDeletedWidget: RecentlyDeletedWidget? = null

    private fun load(): MutableList<WidgetModel> {
        val models = mutableListOf<WidgetModel>()
        Preferences(context).loadWidgets().map { models.add(WidgetTypes.item(it.type)!!.createModel(it, deviceApi)) }
        return models
    }

    private fun save() {
        val toSave = mutableListOf<WidgetConfiguration>()
        models.map { toSave.add(it.configuration) }
        Preferences(context).saveWidgets(toSave)
    }

    val count
        get() = models.count()
    val empty = count == 0

    fun type(atPosition: Int) = models[atPosition].type //TODO utile ?
    fun configuration(atPosition: Int) = models[atPosition].configuration//TODO utile ?

    fun at(position: Int) = models.elementAt(position)

    fun create(configuration: WidgetConfiguration, atPosition: Int = count - 1): Int {
        models.add(
            if (atPosition == -1) count else atPosition,
            WidgetTypes.item(configuration.type)!!.createModel(configuration, deviceApi)
        )
        save()
        return atPosition
    }

    fun delete(atPosition: Int) {
        recentlyDeletedWidget = RecentlyDeletedWidget(atPosition, models[atPosition].configuration)

        models.removeAt(atPosition)
        save()
    }

    fun restore(): Int {
        create(recentlyDeletedWidget!!.configuration, recentlyDeletedWidget!!.position)
        save()
        return recentlyDeletedWidget!!.position
    }

    fun move(fromPosition: Int, toPosition: Int) {
        val movedWidget = models[fromPosition]
        models.removeAt(fromPosition)
        models.add(if (toPosition > fromPosition + 1) toPosition - 1 else toPosition, movedWidget)
        save()
    }

    fun refreshAll(widgetsListViewAdapter: WidgetsRecyclerViewAdapter) {
        for ((position, model) in models.withIndex()) {
            model.refresh(
                onDone = {
                    widgetsListViewAdapter.notifyItemChanged(position)
                },
                onError = {
                    widgetsListViewAdapter.notifyItemChanged(position)
                })
        }
    }
}