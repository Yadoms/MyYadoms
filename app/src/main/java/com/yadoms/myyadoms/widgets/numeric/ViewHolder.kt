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
    private val valueView: TextView = view.findViewById(R.id.value)
    private var value = "-"

    override fun onBind(widget: Preferences.WidgetModel) {
        Log.d("Numeric", "onBind : ${widget.data.name}(id ${widget.data.keywordId})...")

        setName(widget.data.name)

        DeviceApi(YadomsApi(view.context)).getKeyword(widget.data.keywordId,
            onOk = {
                Log.d("Numeric", "onBind/onOk : ${widget.data.name}(id ${widget.data.keywordId}), ${formatValue(it) + formatUnit(it)}")
                setLastUpdate(it.lastAcquisitionDate)
                setValue(formatValue(it) + formatUnit(it))
            }
        ) {
            Log.d("Numeric", "onBind/onError : ${widget.data.name}(id ${widget.data.keywordId}), $it")
            setLastUpdate(null)
        }
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