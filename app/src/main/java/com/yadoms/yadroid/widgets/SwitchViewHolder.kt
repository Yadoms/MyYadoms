package com.yadoms.yadroid.widgets

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.yadoms.yadroid.R
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.yadomsApi.DateHelper
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

        nameView.text = "${widget.name} ${widget.keywordId}" //TODO revoir
        valueView.text = view.resources.getString(R.string.last_update, "-")

        DeviceApi(YadomsApi(Preferences(view.context).serverConnection)).getKeyword(view.context, widget.keywordId, {
            valueView.text = view.resources.getString(
                R.string.last_update,
                DateHelper.dateTimeFromApi(it.lastAcquisitionDate).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT))
            )
            setWidgetImage(it.lastAcquisitionValue == "1")
        }, {})

    }

    fun setWidgetImage(state: Boolean) {
        when (state) {
            true -> buttonView.setBackgroundResource(R.drawable.switch_animation_forward)
            false -> buttonView.setBackgroundResource(R.drawable.switch_animation_reverse)
        }
        switchAnimation = buttonView.background as AnimationDrawable
        switchAnimation.start()
    }

    override fun onClick(p0: View?) {
        state = !state
        setWidgetImage(state)

        widget?.keywordId?.let {
            DeviceApi(YadomsApi(Preferences(view.context).serverConnection)).command(
                view.context,
                it,
                if (state) "1" else "0", {}, {})
        }
    }
}