package com.yadoms.yadroid.widgets.switch_

import com.yadoms.yadroid.R
import com.yadoms.yadroid.widgets.WidgetTypes
import com.yadoms.yadroid.yadomsApi.DeviceApi

val type = WidgetTypes.WidgetTypeItem(
        type = WidgetTypes.WidgetType.Switch,
        nameRessourceId = R.string.switch_name,
        descriptionRessourceId = R.string.switch_description,
        logo = R.drawable.ic_switch1,
        keywordFilter = WidgetTypes.KeywordFilter(expectedKeywordType = arrayOf(DeviceApi.KeywordTypes.Bool)),
        layout = R.layout.widget_switch_item,
        createViewHolder = { ViewHolder(it) }
)
