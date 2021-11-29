package com.yadoms.myyadoms.widgets

import com.yadoms.myyadoms.WidgetConfiguration
import com.yadoms.myyadoms.preferences.Preferences
import com.yadoms.myyadoms.yadomsApi.DeviceApi

abstract class WidgetModel(var configuration: WidgetConfiguration) {

    abstract fun refresh(
        onDone: () -> Unit,
        onError: () -> Unit
    )

    val type
        get() = configuration.type

    val name
        get() = configuration.name

    open fun load(toLoad: String) {
        configuration = Preferences.moshi.adapter(WidgetConfiguration::class.java).fromJson(toLoad)!!
    }

    open fun save(): String {
        return Preferences.moshi.adapter(WidgetConfiguration::class.java).toJson(configuration)
    }
}