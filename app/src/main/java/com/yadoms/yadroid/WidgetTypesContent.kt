package com.yadoms.yadroid

import java.util.*


object WidgetTypesContent {

    /**
     * An array of sample (dummy) items.
     */
    val WIDGET_TYPES: MutableList<WidgetTypeItem> = ArrayList()

    init {
        addItem(WidgetTypeItem("Switch", "A widget to drive ON/OFF devices"))
        addItem(WidgetTypeItem("Numeric display", "A widget to display numeric data"))
    }

    private fun addItem(item: WidgetTypeItem) = WIDGET_TYPES.add(item)

    /**
     * The widget type item
     */
    data class WidgetTypeItem(val name: String, val description: String) {
        override fun toString(): String = name
    }
}