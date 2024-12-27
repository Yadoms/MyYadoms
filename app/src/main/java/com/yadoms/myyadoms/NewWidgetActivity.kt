package com.yadoms.myyadoms

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.yadoms.myyadoms.databinding.ActivityNewWidgetBinding
import com.yadoms.myyadoms.preferences.Preferences
import com.yadoms.myyadoms.widgets.WidgetTypes
import com.yadoms.myyadoms.yadomsApi.DeviceApi


class NewWidgetActivity : AppCompatActivity() {

    var selectedWidgetType: WidgetTypes.WidgetTypeItem? = null
    var selectedDeviceId: Int? = null
    var selectedDeviceName: String? = null
    val preselectedKeywords: MutableList<DeviceApi.Keyword> = mutableListOf()
    var selectedKeywordId: Int? = null
    var selectedKeywordName: String? = null
    var askForName: Boolean = true

    private lateinit var windowTitle: String
    private lateinit var filteredWidgetTypes: ArrayList<WidgetTypes.WidgetType>
    private lateinit var binding: ActivityNewWidgetBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        windowTitle = intent.extras!!.getString("windowTitle")!!
        filteredWidgetTypes = intent.extras!!.get("filteredWidgetTypes") as ArrayList<WidgetTypes.WidgetType>
        askForName = intent.extras!!.getBoolean("askForName")

        binding = ActivityNewWidgetBinding.inflate(layoutInflater)
        val view = binding.root

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.new_widget_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (filteredWidgetTypes.size == 1) {
            selectedWidgetType = WidgetTypes.item(filteredWidgetTypes[0])
            val graph = navHostFragment.navController.navInflater.inflate(R.navigation.new_widget_nav_graph)
            graph.setStartDestination(R.id.deviceFragment)
            navController.setGraph(graph, null)
        }

        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = windowTitle
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            android.R.id.home -> {
                if (!navController.navigateUp())
                    finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setOperationDescription(textId: Int) {
        findViewById<TextView>(R.id.new_widget_operation_description).text = getString(textId)
    }

    fun finish(name: String) {
        val returnIntent = Intent()
        returnIntent.putExtra(
            NewWidgetActivityContract.ID, Preferences.moshi.adapter(Preferences.WidgetData::class.java).toJson(
                selectedWidgetType?.let { w -> selectedKeywordId?.let { k -> Preferences.WidgetData(w.type, name, k) } }
            )
        )
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun startWait() {
        binding.pleaseWaitIndicator.visibility = View.VISIBLE
        binding.newWidgetNavHostFragment.visibility = View.GONE
    }

    fun stopWait() {
        binding.pleaseWaitIndicator.visibility = View.GONE
        binding.newWidgetNavHostFragment.visibility = View.VISIBLE
    }
}

