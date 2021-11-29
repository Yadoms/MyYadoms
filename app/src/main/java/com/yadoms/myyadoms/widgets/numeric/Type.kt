package com.yadoms.myyadoms.widgets.numeric

import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.WidgetConfiguration
import com.yadoms.myyadoms.widgets.WidgetTypes
import com.yadoms.myyadoms.yadomsApi.DeviceApi

val type = WidgetTypes.WidgetTypeItem(
    type = WidgetTypes.WidgetType.Numeric,
    nameRessourceId = R.string.numeric_name,
    descriptionRessourceId = R.string.numeric_description,
    logo = R.drawable.ic_numeric,
    keywordFilter = WidgetTypes.KeywordFilter(expectedKeywordType = arrayOf(DeviceApi.KeywordTypes.Numeric)),
    layout = R.layout.widget_numeric_item,
    createViewHolder = { ViewHolder(it) },
    createModel = { configuration, deviceApi -> Model(configuration, deviceApi) }
)
