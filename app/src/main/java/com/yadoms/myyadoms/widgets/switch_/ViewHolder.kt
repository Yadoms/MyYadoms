package com.yadoms.myyadoms.widgets.switch_

import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.preferences.Preferences
import com.yadoms.myyadoms.widgets.WidgetViewHolder
import com.yadoms.myyadoms.yadomsApi.DeviceApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi

class ViewHolder(view: View) : WidgetViewHolder(view) {
    private var switchAnimation: AnimationDrawable
    private val buttonView = view.findViewById<ImageView>(R.id.button).apply {
        setBackgroundResource(R.drawable.switch_animation_forward)
        switchAnimation = background as AnimationDrawable
    }
    private var state = false
    private var widget: Preferences.WidgetModel? = null

    init {
        itemView.setOnClickListener {
            setState(!state)

            widget?.let {
                DeviceApi(YadomsApi(view.context)).command(
                    it.data.keywordId,
                    if (state) "1" else "0",
                    {}) {}
            }
        }
    }

    override fun onBind(widget: Preferences.WidgetModel) {
        this.widget = widget
        Log.d("Switch", "onBind : ${widget.data.name}(id ${widget.data.keywordId})...")

        setName(widget.data.name)

        DeviceApi(YadomsApi(view.context)).getKeyword(widget.data.keywordId,
            onOk = {
                Log.d("Switch", "onBind/onOk : ${widget.data.name}(id ${widget.data.keywordId}), ${it.lastAcquisitionValue}")
                setLastUpdate(it.lastAcquisitionDate)
                setState(it.lastAcquisitionValue == "1")
            }
        ) {
            Log.d("Switch", "onBind/onError : ${widget.data.name}(id ${widget.data.keywordId}), $it")
            setLastUpdate(null)
        }
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
}