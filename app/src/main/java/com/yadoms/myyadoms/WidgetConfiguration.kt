package com.yadoms.myyadoms

import com.yadoms.myyadoms.widgets.WidgetTypes

data class WidgetConfiguration(
    val type: WidgetTypes.WidgetType,
    val name: String,
    val specificConfiguration: String,
)