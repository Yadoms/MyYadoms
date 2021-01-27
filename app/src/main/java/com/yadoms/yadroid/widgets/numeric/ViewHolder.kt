package com.yadoms.yadroid.widgets.numeric

import android.view.View
import android.widget.TextView
import com.yadoms.yadroid.R
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.widgets.WidgetViewHolder
import com.yadoms.yadroid.yadomsApi.DeviceApi
import com.yadoms.yadroid.yadomsApi.YadomsApi

class ViewHolder(view: View) : WidgetViewHolder(view) {
    private val valueView: TextView = view.findViewById(R.id.value)
    private var state = "-"

    override fun onBind(widget: Preferences.Widget) {
        setName(widget.name)
        DeviceApi(YadomsApi(Preferences(view.context).serverConnection)).getKeyword(view.context, widget.keywordId, {
            setLastUpdate(it.lastAcquisitionDate)

            setState(it.lastAcquisitionValue)
        }, {
            setLastUpdate(null)
        })
    }

    private fun setState(newState: String) {
        if (newState == state)
            return
        state = newState

        valueView.text = newState
    }
}