package com.yadoms.myyadoms.widgets.switch_

import android.graphics.drawable.AnimationDrawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.widgets.WidgetModel
import com.yadoms.myyadoms.widgets.WidgetViewHolder

class ViewHolder(view: View) : WidgetViewHolder(view) {
    private var switchAnimation: AnimationDrawable
    private val buttonView = view.findViewById<ImageView>(R.id.button).apply {
        setBackgroundResource(R.drawable.switch_animation_forward)
        switchAnimation = background as AnimationDrawable
    }
    private var state = false

    init {
        itemView.setOnClickListener {
            setState(!state)

            //TODO
//            widget?.let {
//                DeviceApi(YadomsApi(view.context)).command(
//                    it.data.keywordId,
//                    if (state) "1" else "0",
//                    {}) {}
//            }
        }
    }

    override fun onBind(model: WidgetModel) {
        val keyword = (model as Model).keyword

        Log.d("Switch", "onBind : ${model.name}(id ${keyword.id})...")

        setName(model.name)

        if (keyword.lastAcquisitionDate == null) {
            setLastUpdate(null)
            return
        }

        setLastUpdate(keyword.lastAcquisitionDate)
        setState(keyword.lastAcquisitionValue == "1")
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