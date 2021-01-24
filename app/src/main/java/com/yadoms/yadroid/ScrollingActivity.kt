package com.yadoms.yadroid

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.yadoms.yadroid.databinding.ActivityScrollingBinding
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.preferences.SettingsActivity
import java.util.*
import kotlin.concurrent.schedule


class ScrollingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScrollingBinding
    private var widgets = listOf<Preferences.Widget>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)
        with(binding) {
            toolbarLayout.title = title
            addWidget.setOnClickListener {
                val intent = Intent(this@ScrollingActivity, NewWidgetActivity::class.java)
                startActivity(intent)
            }
        }

        widgets = Preferences(this).widgets

        binding.contentScrollingLayout.widgetsList.layoutManager = LinearLayoutManager(this)
        binding.contentScrollingLayout.widgetsList.adapter = WidgetsRecyclerViewAdapter(widgets)

        Timer(false).schedule(30000, 30000) {
            runOnUiThread { binding.contentScrollingLayout.widgetsList.adapter?.notifyDataSetChanged() }
        }

//        binding.contentScrollingLayout.swipeContainer.setOnRefreshListener {
//            //TODO refresh widgets
//            Log.d("test", "test")
//            sleep(500)
//            binding.contentScrollingLayout.swipeContainer.isRefreshing = false;
//        }
    }

    override fun onResume() {
        val newWidgets = Preferences(this).widgets
        if (widgets != newWidgets) {
            widgets = newWidgets
            binding.contentScrollingLayout.widgetsList.adapter = WidgetsRecyclerViewAdapter(widgets)
        }

        binding.contentScrollingLayout.widgetsList.adapter?.notifyDataSetChanged()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this@ScrollingActivity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}