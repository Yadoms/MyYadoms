package com.yadoms.myyadoms.widgets.numeric

import android.util.Log
import android.view.View
import android.widget.TextView
import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.widgets.WidgetModel
import com.yadoms.myyadoms.widgets.WidgetViewHolder
import com.yadoms.myyadoms.yadomsApi.DeviceApi

class ViewHolder(view: View) : WidgetViewHolder(view) {
    private val valueView: TextView = view.findViewById(R.id.value)
    private var value = "-"

    override fun onBind(model: WidgetModel) {
        (model as Model).refresh(
            onDone = {
                val keyword = model.keyword

                Log.d("Numeric", "onBind : ${model.name}(id ${keyword.id})...")

                setName(model.name)

                if (keyword.lastAcquisitionValue.isEmpty()) {
                    setLastUpdate(null)
                } else {
                    setLastUpdate(keyword.lastAcquisitionDate)
                    setValue(formatValue(keyword) + formatUnit(keyword))
                }
            },
            onError = {
                //TODO faire un truc
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