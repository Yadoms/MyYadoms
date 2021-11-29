package com.yadoms.myyadoms.widgets.numeric

import android.util.Log
import com.yadoms.myyadoms.WidgetConfiguration
import com.yadoms.myyadoms.widgets.WidgetModel
import com.yadoms.myyadoms.yadomsApi.DeviceApi

class Model(configuration: WidgetConfiguration, val deviceApi: DeviceApi) : WidgetModel(configuration) {
    lateinit var keyword: DeviceApi.Keyword
    var configuredKeywordId: Int = configuration.specificConfiguration.toInt()

    override fun load(toLoad: String) { //TODO utile ?
        super.load(toLoad)
        // Add here specific data to load for this widget
    }

    override fun save(): String { //TODO utile ?
        // Add here specific data to save for this widget
        return super.save()
    }

    override fun refresh(
        onDone: () -> Unit,
        onError: () -> Unit
    ) {
        Log.d("Numeric", "refresh : ${name}(id ${configuredKeywordId})...")

        deviceApi.getKeyword(configuredKeywordId,
            onOk = {
                Log.d("Numeric", "onBind/onOk : ${name}(id ${configuredKeywordId})")
                keyword = it //TODO pas terrible de tout écraser
                onDone()
            },
            onError = {
                Log.d("Numeric", "onBind/onError : ${name}(id ${configuredKeywordId}), $it")
                keyword = DeviceApi.Keyword( //TODO pas terrible de tout écraser
                    keyword.id,
                    keyword.deviceId,
                    keyword.capacityName,
                    keyword.friendlyName,
                    "",
                    null,
                    keyword.accessMode,
                    keyword.type,
                    keyword.units
                )
                onError()
            })
    }

    //TODO ajouter les données génériques de WidgetModel en aggregation et non héritage
}
