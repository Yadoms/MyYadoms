package com.yadoms.yadroid

import com.yadoms.yadroid.yadomsApi.DeviceApi
import java.util.*


object WidgetTypes {

    val WidgetTypes: MutableList<WidgetTypeItem> = ArrayList()

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

    init {
        addItem(
            WidgetTypeItem(
                WidgetType.Switch,
                "Switch",
                "A widget to drive ON/OFF devices",
                R.drawable.ic_switch,
                KeywordFilter(expectedKeywordType = arrayOf(DeviceApi.KeywordTypes.Bool))
            )
        )
        addItem(
            WidgetTypeItem(
                WidgetType.Numeric,
                "Numeric display",
                "A widget to display numeric data",
                R.drawable.ic_numeric,
                KeywordFilter(expectedKeywordType = arrayOf(DeviceApi.KeywordTypes.Numeric))
            )
        )
    }

    private fun addItem(item: WidgetTypeItem) = WidgetTypes.add(item)

    fun item(byType: WidgetType): WidgetTypeItem? {
        return WidgetTypes.find { it.type == byType }
    }

    /**
     * The widget type item
     */
    enum class WidgetType { Switch, Numeric }
    data class WidgetTypeItem(val type: WidgetType, val name: String, val description: String, val logo: Int, val keywordFilter: KeywordFilter) {
        override fun toString(): String = name
    }
}