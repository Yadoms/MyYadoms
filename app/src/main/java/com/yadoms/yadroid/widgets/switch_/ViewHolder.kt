package com.yadoms.yadroid.widgets.switch_

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.yadoms.yadroid.R
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.widgets.WidgetViewHolder
import com.yadoms.yadroid.yadomsApi.DeviceApi
import com.yadoms.yadroid.yadomsApi.YadomsApi

class ViewHolder(view: View) : WidgetViewHolder(view), View.OnClickListener {
    private var switchAnimation: AnimationDrawable
    private val buttonView = view.findViewById<ImageView>(R.id.button).apply {
        setBackgroundResource(R.drawable.switch_animation_forward)
        switchAnimation = background as AnimationDrawable
    }
    private val nameView: TextView = view.findViewById(R.id.name)
    private var state = false
    private var widget: Preferences.Widget? = null

    init {
        itemView.setOnClickListener(this)
    }

    override fun onBind(widget: Preferences.Widget) {
        this.widget = widget

        nameView.text = widget.name

        DeviceApi(YadomsApi(Preferences(view.context).serverConnection)).getKeyword(view.context, widget.keywordId, {
            setLastUpdate(it.lastAcquisitionDate)
            setState(it.lastAcquisitionValue == "1")
        }, {
            setLastUpdate(null)
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