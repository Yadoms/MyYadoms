package com.yadoms.yadroid.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yadoms.yadroid.WidgetTypes

class Preferences(private val context: Context) {

    private val sharedPreference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(context)

    data class ServerConnection(
        val url: String,
        val port: Int,
        val useBasicAuthentication: Boolean,
        val basicAuthenticationUser: String,
        val basicAuthenticationPassword: String,
        val useHttps: Boolean,
        val httpsPort: Int
    )

    val serverConnection: ServerConnection
        get() = ServerConnection(
            sharedPreference.getString("server_url", "") ?: "",
            (sharedPreference.getString("server_port", "8080") ?: "8080").toInt(),
            sharedPreference.getBoolean("server_use_basic_authentication", false),
            sharedPreference.getString("server_basic_authentication_username", "") ?: "",
            sharedPreference.getString("server_basic_authentication_password", "") ?: "",
            sharedPreference.getBoolean("server_use_https", false),
            (sharedPreference.getString("server_https_port", "443") ?: "443").toInt()
        )

    data class Widget(val type: WidgetTypes.WidgetType, val name: String, val keywordId: Int)
    class WidgetsPreferences(val widgets: List<Widget>)

    fun addNewWidget(widget: Widget) {
        val currentWidgets = widgets.toMutableList()
        currentWidgets.add(widget)

        val widgetsPreferencesString = moshi.adapter(WidgetsPreferences::class.java).toJson(WidgetsPreferences(currentWidgets))

        val preferencesEditor = sharedPreference.edit()
        preferencesEditor.putString("widgets", widgetsPreferencesString)
        preferencesEditor.apply()
        preferencesEditor.commit()
    }

    val widgets: List<Widget>
        get() {
            val widgetsPreferencesString = sharedPreference.getString("widgets", "") ?: return listOf()
            if (widgetsPreferencesString.isEmpty())
                return listOf()

            val widgetsPreferences = moshi.adapter(WidgetsPreferences::class.java).fromJson(widgetsPreferencesString) ?: return listOf()
            return widgetsPreferences.widgets
        }

    companion object {
        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }
}