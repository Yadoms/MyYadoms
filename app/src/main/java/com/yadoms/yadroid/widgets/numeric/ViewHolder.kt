package com.yadoms.yadroid.widgets.numeric

import android.view.View
import android.widget.TextView
import com.yadoms.yadroid.R
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.widgets.WidgetViewHolder
import com.yadoms.yadroid.yadomsApi.DeviceApi
import com.yadoms.yadroid.yadomsApi.YadomsApi
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ViewHolder(view: View) : WidgetViewHolder(view) {
    private val valueView: TextView = view.findViewById(R.id.value)
    private var value = "-"

    override fun onBind(widget: Preferences.Widget) {
        setName(widget.name)
        DeviceApi(YadomsApi(Preferences(view.context).serverConnection)).getKeyword(view.context, widget.keywordId, {
            setLastUpdate(it.lastAcquisitionDate)
            setValue(formatValue(it))
        }, {
            setLastUpdate(null)
        })
    }

    private fun formatValue(keyword: DeviceApi.Keyword): String {
        if (keyword.lastAcquisitionValue.isEmpty())
            return "-"

        return when (keyword.capacityName) {
            DeviceApi.StandardCapacities.temperature -> "%.1f".format(keyword.lastAcquisitionValue.toFloat()) + keyword.units
            else -> keyword.lastAcquisitionValue + keyword.units
        }
    }

    private fun setValue(newValue: String) {
        if (newValue == value)
            return
        value = newValue

        valueView.text = newValue
    }
}