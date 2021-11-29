package com.yadoms.myyadoms.widgets.switch_

import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.widgets.WidgetTypes
import com.yadoms.myyadoms.widgets.numeric.Model
import com.yadoms.myyadoms.yadomsApi.DeviceApi

val type = WidgetTypes.WidgetTypeItem(
    type = WidgetTypes.WidgetType.Switch,
    nameRessourceId = R.string.switch_name,
    descriptionRessourceId = R.string.switch_description,
    logo = R.drawable.ic_switch1,
    keywordFilter = WidgetTypes.KeywordFilter(
        expectedKeywordType = arrayOf(DeviceApi.KeywordTypes.Bool),
        expectedKeywordAccess = arrayOf(DeviceApi.KeywordAccess.GetSet)
    ),
    layout = R.layout.widget_switch_item,
    createViewHolder = { ViewHolder(it) },
    createModel = { configuration, deviceApi -> Model(configuration, deviceApi) }
)
