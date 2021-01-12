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
                "Switch",
                "A widget to drive ON/OFF devices",
                KeywordFilter(expectedKeywordType = arrayOf(DeviceApi.KeywordTypes.Bool))
            )
        )
        addItem(
            WidgetTypeItem(
                "Numeric display",
                "A widget to display numeric data",
                KeywordFilter(expectedKeywordType = arrayOf(DeviceApi.KeywordTypes.Numeric))
            )
        )
    }

    private fun addItem(item: WidgetTypeItem) = WidgetTypes.add(item)

    /**
     * The widget type item
     */
    data class WidgetTypeItem(val name: String, val description: String, val keywordFilter: KeywordFilter) {
        override fun toString(): String = name
    }
}