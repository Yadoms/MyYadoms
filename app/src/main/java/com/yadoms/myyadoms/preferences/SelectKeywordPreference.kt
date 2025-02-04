package com.yadoms.myyadoms.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference
import com.yadoms.myyadoms.R
import com.yadoms.myyadoms.yadomsApi.DeviceApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi

class SelectKeywordPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {
    init {
        dialogLayoutResource = R.layout.select_away_from_home_keyword_dialog
        dialogTitle = context.getString(R.string.select_a_device)
        positiveButtonText = context.getString(R.string.select)
        negativeButtonText = context.getString(R.string.cancel)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        updateSummary(getPersistedKeywordId(defaultValue as? Int ?: 0))
    }

    private fun updateSummary(keywordId: Int) {
        DeviceApi(YadomsApi(context)).getKeyword(keywordId,
            onOk = { keyword ->
                DeviceApi(YadomsApi(context)).getDevice(keyword.deviceId,
                    onOk = { device ->
                        summary = buildString {
                            append(device.friendlyName)
                            append(" - ")
                            append(keyword.friendlyName)
                        }
                    },
                    onError = {
                        summary = context.getString(R.string.unknown_device)
                    })
            },
            onError = {
                summary = context.getString(R.string.unknown_device)
            })



        summary = context.getString(R.string.unknown_device)
    }

    fun getPersistedKeywordId(defaultValue: Int): Int {
        return getPersistedInt(defaultValue)
    }

    fun persistKeywordId(keywordId: Int): Boolean {
        persistInt(keywordId)
        updateSummary(keywordId)
        return true
    }
}