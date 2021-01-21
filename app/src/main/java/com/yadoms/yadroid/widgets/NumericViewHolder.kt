package com.yadoms.yadroid.widgets

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.yadoms.yadroid.R
import com.yadoms.yadroid.preferences.Preferences

class NumericViewHolder(view: View) : WidgetViewHolder(view) {
    val buttonView: Button = view.findViewById(R.id.button)
    val nameView: TextView = view.findViewById(R.id.name)
    override val name: String
        get() = "Numeric"

    override fun onBind(widget: Preferences.Widget) {
        buttonView.text = widget.name
        nameView.text = widget.name + widget.keywordId.toString()
    }
}