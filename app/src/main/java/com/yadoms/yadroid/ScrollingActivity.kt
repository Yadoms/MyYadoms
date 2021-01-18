package com.yadoms.yadroid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.yadoms.yadroid.databinding.ActivityScrollingBinding
import com.yadoms.yadroid.preferences.Preferences
import com.yadoms.yadroid.preferences.SettingsActivity

class ScrollingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScrollingBinding

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

        val widgets = Preferences(this).widgets
        
        binding.contentScrollingLayout.widgetsList.layoutManager = LinearLayoutManager(this)
        binding.contentScrollingLayout.widgetsList.adapter = WidgetSwitchRecyclerViewAdapter(widgets, object : WidgetSwitchRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                Log.d("MainActivity", "widget clicked ! " + position.toString())
            }
        })
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