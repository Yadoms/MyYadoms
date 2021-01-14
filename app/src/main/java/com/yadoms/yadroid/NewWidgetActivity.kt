package com.yadoms.yadroid

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.yadoms.yadroid.databinding.ActivityNewWidgetBinding
import com.yadoms.yadroid.yadomsApi.DeviceApi
import java.io.StringReader

class NewWidgetActivity : AppCompatActivity() {

    val preselectedKeywords: MutableList<DeviceApi.Keyword> = mutableListOf()
    var selectedDeviceId: Int? = null
    var selectedWidgetType: WidgetTypes.WidgetTypeItem? = null

    private lateinit var binding: ActivityNewWidgetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewWidgetBinding.inflate(layoutInflater)
        val view = binding.root

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        setContentView(view)

        supportActionBar?.title = "Add new widget"
    }

    fun addNewWidget(selectedKeywordId: Int) {
        val klaxon = Klaxon()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val widgetsPreference = preferences.getString("widgets", "")
        val widgetsJsonArray = if (widgetsPreference?.isNotEmpty() == true) klaxon.parseJsonArray(StringReader(widgetsPreference)) else JsonArray<JsonObject>()

        (widgetsJsonArray as JsonArray<JsonObject>).add(JsonObject(mapOf("name" to selectedWidgetType!!.name, "keywordId" to selectedKeywordId)))
        val preferencesEditor = preferences.edit()
        preferencesEditor.putString("widgets", widgetsJsonArray.toJsonString())
        preferencesEditor.apply();
        preferencesEditor.commit();
    }

}