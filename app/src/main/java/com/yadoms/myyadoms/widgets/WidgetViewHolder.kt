package com.yadoms.myyadoms.widgets

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.myyadoms.MyYadomsApp
import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.preferences.Preferences
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.time.DurationUnit
import kotlin.time.toDuration

abstract class WidgetViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val nameView: TextView = view.findViewById(R.id.name)
    private val lastUpdateView: TextView = view.findViewById(R.id.last_update)

    abstract fun onBind(widget: Preferences.WidgetModel)

    fun setName(name: String) {
        nameView.text = name
    }

    private fun formatLastAcquisitionDate(dt: LocalDateTime): String {
        val serverTime = (view.context.applicationContext as MyYadomsApp).serverTime.now() ?: return "{-}"

        val elapsedSeconds = serverTime.toInstant(ZoneOffset.UTC).epochSecond - dt.toInstant(ZoneOffset.UTC).epochSecond
        val duration = elapsedSeconds.toDuration(DurationUnit.SECONDS)

        return if (duration.isInfinite() || duration.isNegative())
            "{-}"
        else if (duration.inWholeSeconds < 60)
            view.resources.getString(R.string.just_now)
        else if (duration.inWholeMinutes < 60)
            view.resources.getString(R.string.x_minutes, duration.inWholeMinutes)
        else if (duration.inWholeHours < 24)
            view.resources.getString(R.string.x_hours, duration.inWholeHours)
        else
            view.resources.getString(R.string.more_than_one_day)
    }

    fun setLastUpdate(lastAcquisitionDate: LocalDateTime?) {
        val lastUpdateAsDuration = Preferences(this.view.context).display.lastUpdateAsDuration
        lastUpdateView.text = view.resources.getString(
            R.string.last_update,
            when (lastAcquisitionDate) {
                null -> "{-}"
                else -> when (lastUpdateAsDuration) {
                    true -> formatLastAcquisitionDate(lastAcquisitionDate)
                    false -> lastAcquisitionDate.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT))
                }
            }
        )
    }
}