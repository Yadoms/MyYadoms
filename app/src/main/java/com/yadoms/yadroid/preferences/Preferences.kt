package com.yadoms.yadroid.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.yadoms.yadroid.WidgetTypes
import java.io.StringReader

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

    fun addNewWidget(widget: Widget) {
        val klaxon = Klaxon()
        val widgetsPreference = sharedPreference.getString("widgets", "")
        val widgetsJsonArray =
            if (widgetsPreference?.isNotEmpty() == true) klaxon.parseJsonArray(StringReader(widgetsPreference)) else JsonArray<JsonObject>()

        (widgetsJsonArray as JsonArray<JsonObject>).add(
            JsonObject(
                mapOf(
                    "widgetType" to widget.type.ordinal,
                    "name" to widget.name,
                    "keywordId" to widget.keywordId
                )
            )
        )
        val preferencesEditor = sharedPreference.edit()
        preferencesEditor.putString("widgets", widgetsJsonArray.toJsonString())
        preferencesEditor.apply();
        preferencesEditor.commit();
    }

    data class Widget(val type: WidgetTypes.WidgetType, val name: String, val keywordId: Int)

    val widgets: List<Widget>
        get() {
            val klaxon = Klaxon()
            val widgetsPreference = sharedPreference.getString("widgets", "")
            val widgetsJsonArray =
                if (widgetsPreference?.isNotEmpty() == true) klaxon.parseJsonArray(StringReader(widgetsPreference)) else JsonArray<JsonObject>()
            return widgetsJsonArray.map {
                Widget(
                    WidgetTypes.WidgetType.values()[(it as JsonObject)["widgetType"] as Int],
                    it["name"] as String,
                    it["keywordId"] as Int
                )
            }
        }
}