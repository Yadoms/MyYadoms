package com.yadoms.yadroid.widgets

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.yadoms.yadroid.R
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.yadomsApi.DeviceApi
import com.yadoms.yadroid.yadomsApi.YadomsApi
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class SwitchViewHolder(val view: View) : WidgetViewHolder(view), View.OnClickListener {
    private var switchAnimation: AnimationDrawable
    private val buttonView = view.findViewById<ImageView>(R.id.button).apply {
        setBackgroundResource(R.drawable.switch_animation_forward)
        switchAnimation = background as AnimationDrawable
    }
    private val nameView: TextView = view.findViewById(R.id.name)
    private val valueView: TextView = view.findViewById(R.id.value)
    private var state = false
    private var widget: Preferences.Widget? = null

    init {
        itemView.setOnClickListener(this)
    }

    override val name: String
        get() = "Switch"

    override fun onBind(widget: Preferences.Widget) {
        this.widget = widget

        nameView.text = widget.name

        DeviceApi(YadomsApi(Preferences(view.context).serverConnection)).getKeyword(view.context, widget.keywordId, {
            valueView.text = view.resources.getString(
                R.string.last_update,
                when (it.lastAcquisitionDate) {
                    null -> "{-}"
                    else -> it.lastAcquisitionDate.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT))
                }
            )
            setState(it.lastAcquisitionValue == "1")
        }, {
            valueView.text = view.resources.getString(R.string.last_update, "-")
        })

    }

    private fun setState(newState: Boolean) {
        if (newState == state)
            return
        state = newState

        when (state) {
            true -> buttonView.setBackgroundResource(R.drawable.switch_animation_forward)
            false -> buttonView.setBackgroundResource(R.drawable.switch_animation_reverse)
        }
        switchAnimation = buttonView.background as AnimationDrawable
        switchAnimation.start()
    }

    override fun onClick(p0: View?) {
        setState(!state)

        widget?.let {
            DeviceApi(YadomsApi(Preferences(view.context).serverConnection)).command(
                view.context,
                it.keywordId,
                if (state) "1" else "0", {}, {})
        }
    }
}