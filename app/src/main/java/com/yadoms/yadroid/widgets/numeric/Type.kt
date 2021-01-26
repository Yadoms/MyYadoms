package com.yadoms.yadroid.widgets.numeric

import com.yadoms.yadroid.R
import com.yadoms.yadroid.widgets.WidgetTypes
import com.yadoms.yadroid.yadomsApi.DeviceApi

val type = WidgetTypes.WidgetTypeItem(
    type = WidgetTypes.WidgetType.Numeric,
    name = "Numeric display",
    description = "A widget to display numeric data",
    logo = R.drawable.ic_numeric,
    keywordFilter = WidgetTypes.KeywordFilter(expectedKeywordType = arrayOf(DeviceApi.KeywordTypes.Numeric)),
    layout = R.layout.widget_numeric_item,
    createViewHolder = { ViewHolder(it) }
)
