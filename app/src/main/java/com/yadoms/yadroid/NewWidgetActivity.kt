package com.yadoms.yadroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.yadoms.yadroid.databinding.ActivityNewWidgetBinding
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.widgets.WidgetTypes
import com.yadoms.yadroid.yadomsApi.DeviceApi

class NewWidgetActivity : AppCompatActivity() {

    var selectedWidgetType: WidgetTypes.WidgetTypeItem? = null
    var selectedDeviceId: Int? = null
    var selectedDeviceName: String? = null
    val preselectedKeywords: MutableList<DeviceApi.Keyword> = mutableListOf()
    var selectedKeywordId: Int? = null
    var selectedKeywordName: String? = null

    private lateinit var binding: ActivityNewWidgetBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewWidgetBinding.inflate(layoutInflater)
        val view = binding.root

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_new_widget)
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
            NewWidgetActivityContract.ID, Preferences.moshi.adapter(Preferences.Widget::class.java).toJson(
                Preferences.Widget(selectedWidgetType!!.type, name, selectedKeywordId!!)
            )
        )
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}

