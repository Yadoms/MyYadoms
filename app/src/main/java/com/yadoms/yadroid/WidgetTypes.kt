package com.yadoms.yadroid

import android.view.View
import com.yadoms.yadroid.widgets.NumericViewHolder
import com.yadoms.yadroid.widgets.SwitchViewHolder
import com.yadoms.yadroid.widgets.WidgetViewHolder
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
                type = WidgetType.Switch,
                name = "Switch",
                description = "A widget to drive ON/OFF devices",
                logo = R.drawable.ic_switch1,
                keywordFilter = KeywordFilter(expectedKeywordType = arrayOf(DeviceApi.KeywordTypes.Bool)),
                layout = R.layout.widget_switch_item,
                createViewHolder = { SwitchViewHolder(it) }
            )
        )
        addItem(
            WidgetTypeItem(
                type = WidgetType.Numeric,
                name = "Numeric display",
                description = "A widget to display numeric data",
                logo = R.drawable.ic_numeric,
                keywordFilter = KeywordFilter(expectedKeywordType = arrayOf(DeviceApi.KeywordTypes.Numeric)),
                layout = R.layout.widget_numeric_item,
                createViewHolder = { NumericViewHolder(it) }
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
    data class WidgetTypeItem(
        val type: WidgetType,
        val name: String,
        val description: String,
        val logo: Int,
        val keywordFilter: KeywordFilter,
        val layout: Int,
        val createViewHolder: (View) -> WidgetViewHolder
    ) {
        override fun toString(): String = name
    }
}