package com.yadoms.myyadoms.widgets

import android.view.View
import com.yadoms.myyadoms.WidgetConfiguration
import com.yadoms.myyadoms.yadomsApi.DeviceApi


object WidgetTypes {

    val WidgetTypes = mutableListOf(
        com.yadoms.myyadoms.widgets.switch_.type,
        com.yadoms.myyadoms.widgets.numeric.type
    )

    data class KeywordFilter(
        var expectedKeywordType: Array<DeviceApi.KeywordTypes> = arrayOf(),
        var expectedCapacity: Array<DeviceApi.StandardCapacities> = arrayOf(),
        var expectedKeywordAccess: Array<DeviceApi.KeywordAccess> = arrayOf()
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as KeywordFilter

            if (!expectedKeywordType.contentEquals(other.expectedKeywordType)) return false
            if (!expectedCapacity.contentEquals(other.expectedCapacity)) return false
            if (!expectedKeywordAccess.contentEquals(other.expectedKeywordAccess)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = expectedKeywordType.contentHashCode()
            result = 31 * result + expectedCapacity.contentHashCode()
            result = 31 * result + expectedKeywordAccess.contentHashCode()
            return result
        }
    }

    fun item(byType: WidgetType): WidgetTypeItem? {
        return WidgetTypes.find { it.type == byType }
    }

    /**
     * The widget type item
     */
    enum class WidgetType { Switch, Numeric }
    data class WidgetTypeItem(
        val type: WidgetType,
        val nameRessourceId: Int,
        val descriptionRessourceId: Int,
        val logo: Int,
        val keywordFilter: KeywordFilter,
        val layout: Int,
        val createViewHolder: (View) -> WidgetViewHolder,
        val createModel: (WidgetConfiguration, DeviceApi) -> WidgetModel
    )
}