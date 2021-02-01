package com.yadoms.yadroid.widgets.numeric

import android.util.Log
import android.view.View
import android.widget.TextView
import com.yadoms.yadroid.R
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.widgets.WidgetViewHolder
import com.yadoms.yadroid.yadomsApi.DeviceApi
import com.yadoms.yadroid.yadomsApi.YadomsApi

class ViewHolder(view: View) : WidgetViewHolder(view) {
    private val valueView: TextView = view.findViewById(R.id.value)
    private var value = "-"

    override fun onBind(widget: Preferences.Widget) {
        Log.d("Numeric", "onBind : ${widget.name} ${adapterPosition}...")

        setName(widget.name)

        DeviceApi(YadomsApi(Preferences(view.context).serverConnection)).getKeyword(view.context, widget.keywordId,
            onOk = {
                Log.d("Numeric", "onBind/onOk : ${widget.name} ${adapterPosition}, ${formatValue(it) + formatUnit(it)}")
                setLastUpdate(it.lastAcquisitionDate)
                setValue(formatValue(it) + formatUnit(it))
            },
            onError = {
                Log.d("Numeric", "onBind/onError : ${widget.name} ${adapterPosition}, $it")
                setLastUpdate(null)
            })
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