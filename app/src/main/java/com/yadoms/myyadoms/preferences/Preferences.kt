package com.yadoms.myyadoms.preferences

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yadoms.myyadoms.widgets.WidgetTypes
import com.yadoms.myyadoms.yadomsApi.DeviceApi

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

    data class AwayFromHome(
        val enable: Boolean,
        val referenceLocation: Location,
        val referenceLocationDistance: Int,
        val device: Int
    )

    val awayFromHome: AwayFromHome
        get() = AwayFromHome(
            sharedPreference.getBoolean("away_from_home_enable", false),
            Location("").apply {
                latitude = sharedPreference.getFloat("reference_location_latitude", 0.0f).toDouble()
                longitude = sharedPreference.getFloat("reference_location_longitude", 0.0f).toDouble()
            },
            sharedPreference.getInt("reference_location_distance", 150),
            sharedPreference.getInt("away_from_home_device_to_control", -1)
        )

    data class Display(
        val lastUpdateAsDuration: Boolean
    )

    val display: Display
        get() = Display(
            sharedPreference.getBoolean("display_last_update_as_duration", false)
        )

    class WidgetData(
        val type: WidgetTypes.WidgetType,
        val name: String,
        val keywordId: Int
    )

    //TODO déplacer
    abstract class WidgetModel(
        val data: WidgetData,
        var lastState: DeviceApi.Keyword? = null
    ) {

        abstract fun requestState()
    }

    class WidgetsPreferences(val widgets: MutableList<WidgetData>)

    val widgets: MutableList<WidgetData>
        get() = loadWidgets()

    private fun loadWidgets(): MutableList<WidgetData> {
        val widgetsPreferencesString = sharedPreference.getString("widgets", "") ?: return mutableListOf()
        if (widgetsPreferencesString.isEmpty())
            return mutableListOf()

        val widgetsPreferences = moshi.adapter(WidgetsPreferences::class.java).fromJson(widgetsPreferencesString) ?: return mutableListOf()
        return widgetsPreferences.widgets
    }

    fun saveWidgets(currentWidgets: MutableList<WidgetData>) {
        val widgetsPreferencesString = moshi.adapter(WidgetsPreferences::class.java).toJson(WidgetsPreferences(currentWidgets))
        val preferencesEditor = sharedPreference.edit()
        preferencesEditor.putString("widgets", widgetsPreferencesString)
        preferencesEditor.apply()
        preferencesEditor.commit()
    }

    companion object {
        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }
}