package com.yadoms.myyadoms.widgets.numeric

import android.util.Log
import android.view.View
import android.widget.TextView
import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.preferences.Preferences
import com.yadoms.myyadoms.widgets.WidgetViewHolder
import com.yadoms.myyadoms.yadomsApi.DeviceApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi

class ViewHolder(view: View) : WidgetViewHolder(view) {
    private val _logTag = javaClass.canonicalName
    private val valueView: TextView = view.findViewById(R.id.value)
    private var value = "-"

    override fun onBind(widget: Preferences.WidgetModel) {
        Log.d(_logTag, "onBind : ${widget.data.name}(id ${widget.data.keywordId})...")

        setName(widget.data.name)

        widget.lastState?.let {
            setValue(formatValue(it) + formatUnit(it))
        }
        setLastUpdate(widget.lastState?.lastAcquisitionDate)
    }

    private fun formatUnit(keyword: DeviceApi.Keyword): String {
        if (keyword.lastAcquisitionValue.isEmpty())
            return ""
        if (keyword.capacityName == DeviceApi.StandardCapacities.duration)
            return ""
        return " " + keyword.units.label
    }

    private fun formatValue(keyword: DeviceApi.Keyword): String {
        if (keyword.lastAcquisitionValue.isEmpty())
            return "-"

        return when (keyword.capacityName) {
            DeviceApi.StandardCapacities.humidity,
            DeviceApi.StandardCapacities.batteryLevel,
            DeviceApi.StandardCapacities.dimmable,
            DeviceApi.StandardCapacities.signalPower,
            DeviceApi.StandardCapacities.illumination -> "%.0f".format(keyword.lastAcquisitionValue.toFloat())
            DeviceApi.StandardCapacities.duration -> keyword.lastAcquisitionValue
            else -> "%.1f".format(keyword.lastAcquisitionValue.toFloat())
        }
    }

    private fun setValue(newValue: String) {
        if (newValue == value)
            return
        value = newValue
        valueView.text = newValue
    }
}