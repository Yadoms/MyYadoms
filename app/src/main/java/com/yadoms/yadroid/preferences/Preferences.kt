RecyclerView.Adapterpackage com.yadoms.yadroid.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yadoms.yadroid.widgets.WidgetTypes

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

    class WidgetData(val type: WidgetTypes.WidgetType, val name: String, val keywordId: Int)

    //TODO déplacer
    abstract class WidgetModel(val data: WidgetData) {
        abstract fun requestState()
    }

    class WidgetsPreferences(val widgets: MutableList<WidgetData>)

    val widgets: MutableList<WidgetModel>
        get() = loadWidgets()

    private fun loadWidgets(): MutableList<WidgetModel> {
        val widgetsPreferencesString = sharedPreference.getString("widgets", "") ?: return mutableListOf()
        if (widgetsPreferencesString.isEmpty())
            return mutableListOf()

        val widgetsPreferences = moshi.adapter(WidgetsPreferences::class.java).fromJson(widgetsPreferencesString) ?: return mutableListOf()
        val widgetModels: MutableList<WidgetModel> = mutableListOf()
        widgetsPreferences.widgets.map {
            val item = WidgetTypes.item(it.type)
            item?.createModel(WidgetData(it.type, it.name, it.keywordId))
            widgetModels.add(WidgetTypes.item(it.type).createModel())
        }
        return widgetModels

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