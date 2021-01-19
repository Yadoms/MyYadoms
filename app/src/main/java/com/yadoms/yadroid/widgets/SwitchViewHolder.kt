package com.yadoms.yadroid.widgets

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.yadoms.yadroid.R
import com.yadoms.yadroid.preferences.Preferences

class SwitchViewHolder(val view: View) : WidgetViewHolder(view), View.OnClickListener {
    private var switchAnimation: AnimationDrawable
    private val buttonView = view.findViewById<ImageView>(R.id.button).apply {
        setBackgroundResource(R.drawable.switch_animation_forward)
        switchAnimation = background as AnimationDrawable
    }
    private val nameView: TextView = view.findViewById(R.id.name)
    private val valueView: TextView = view.findViewById(R.id.value)
    private var state = false
    private val lastUpdateDate = "19/01/2021 à 15:53" //TODO à récupérer de yadomsApi

    init {
        itemView.setOnClickListener(this)
    }

    override val name: String
        get() = "Switch"

    override fun onBind(widget: Preferences.Widget) {
        nameView.text = widget.name + widget.keywordId.toString() //TODO revoir
        valueView.text = view.resources.getString(R.string.last_update, lastUpdateDate)
    }

    override fun onClick(p0: View?) {
        when (state) {
            false -> buttonView.setBackgroundResource(R.drawable.switch_animation_forward)
            true -> buttonView.setBackgroundResource(R.drawable.switch_animation_reverse)
        }
        switchAnimation = buttonView.background as AnimationDrawable
        switchAnimation.start()
        state = !state
    }
}