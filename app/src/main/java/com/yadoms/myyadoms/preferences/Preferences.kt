package com.yadoms.myyadoms.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yadoms.myyadoms.WidgetConfiguration

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
        val httpsPort: Int,
        val ignoreHttpsCertificateError: Boolean
    )

    val serverConnection: ServerConnection
        get() = ServerConnection(
            (sharedPreference.getString("server_url", "") ?: "").trimEnd(),
            (sharedPreference.getString("server_port", "8080") ?: "8080").toInt(),
            sharedPreference.getBoolean("server_use_basic_authentication", false),
            sharedPreference.getString("server_basic_authentication_username", "") ?: "",
            sharedPreference.getString("server_basic_authentication_password", "") ?: "",
            sharedPreference.getBoolean("server_use_https", false),
            (sharedPreference.getString("server_https_port", "443") ?: "443").toInt(),
            sharedPreference.getBoolean("ignore_https_certificate_error", false)
        )

    data class WidgetsPreferences(val widgets: MutableList<WidgetConfiguration>)

    fun loadWidgets(): MutableList<WidgetConfiguration> {
        val widgetsPreferencesString = sharedPreference.getString("widgets", "") ?: return mutableListOf()
        if (widgetsPreferencesString.isEmpty())
            return mutableListOf()

        val widgetsPreferences = moshi.adapter(WidgetsPreferences::class.java).fromJson(widgetsPreferencesString) ?: return mutableListOf()
        return widgetsPreferences.widgets
    }

    fun saveWidgets(currentWidgets: MutableList<WidgetConfiguration>) {
        val widgetsPreferencesString = moshi.adapter(WidgetsPreferences::class.java).toJson(WidgetsPreferences(currentWidgets))
        val preferencesEditor = sharedPreference.edit()
        preferencesEditor.putString("widgets", widgetsPreferencesString)
        preferencesEditor.apply()
    }

    companion object {
        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }
}