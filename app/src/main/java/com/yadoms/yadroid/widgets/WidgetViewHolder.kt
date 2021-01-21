package com.yadoms.yadroid.widgets

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.yadroid.preferences.Preferences

abstract class WidgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract val name: String

    override fun toString(): String {
        return super.toString() + " '" + name + "'"
    }

    abstract fun onBind(widget: Preferences.Widget)
}