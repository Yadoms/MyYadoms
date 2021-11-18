package com.yadoms.myyadoms.widgets

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.preferences.Preferences
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

abstract class WidgetViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val nameView: TextView = view.findViewById(R.id.name)
    private val lastUpdateView: TextView = view.findViewById(R.id.last_update)

    abstract fun onBind(widget: Preferences.WidgetModel)

    fun setName(name: String) {
        nameView.text = name
    }

    fun setLastUpdate(dt: LocalDateTime?) {
        lastUpdateView.text = view.resources.getString(
            R.string.last_update,
            when (dt) {
                null -> "{-}"
                else -> dt.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT))
            }
        )
    }
}