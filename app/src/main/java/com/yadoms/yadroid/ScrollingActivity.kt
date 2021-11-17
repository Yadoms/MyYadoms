package com.yadoms.yadroid

import com.yadoms.yadroid.preferences.Preferences
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yadoms.yadroid.databinding.ActivityScrollingBinding
import com.yadoms.yadroid.preferences.SettingsActivity
import java.util.*
import kotlin.concurrent.schedule


class ScrollingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScrollingBinding

    private val widgetsListView: RecyclerView
        get() = binding.contentScrollingLayout.widgetsList

    private val widgetsListViewAdapter: WidgetsRecyclerViewAdapter
        get() = widgetsListView.adapter as WidgetsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)
        with(binding) {
            toolbarLayout.title = title
            val newWidgetActivityContractLauncher = registerForActivityResult(NewWidgetActivityContract()) { newWidget ->
                newWidget?.let { widgetsListViewAdapter.addNewWidget(it) }
            }
            addWidget.setOnClickListener {
                newWidgetActivityContractLauncher.launch(null)
            }
        }

        binding.versionView.text = getString(R.string.app_version, BuildConfig.VERSION_NAME, if (BuildConfig.DEBUG) "d" else "")

        widgetsListView.layoutManager = LinearLayoutManager(this)
        widgetsListView.adapter = WidgetsRecyclerViewAdapter(this, object : EmptyListener {
            override fun onEmptyChange(empty: Boolean) {
                binding.contentScrollingLayout.widgetsList.visibility = if (empty) GONE else VISIBLE
                binding.contentScrollingLayout.noContent.visibility = if (empty) VISIBLE else GONE
            }
        })
        binding.contentScrollingLayout.widgetsList.visibility = if (Preferences(this).widgets.isEmpty()) GONE else VISIBLE
        binding.contentScrollingLayout.noContent.visibility = if (Preferences(this).widgets.isEmpty()) VISIBLE else GONE

        val itemTouchHelper = ItemTouchHelper(WidgetSwipeAndDragHandler(this, widgetsListViewAdapter))
        itemTouchHelper.attachToRecyclerView(widgetsListView)

        with(binding.contentScrollingLayout.swipeLayout) {
            setColorSchemeColors(getColor(R.color.yadomsBlue))
            setOnRefreshListener {
                widgetsListViewAdapter.notifyDataSetChanged()
                isRefreshing = false
            }
        }

        if (Preferences(this).serverConnection.url.isEmpty()) {
            Snackbar.make(
                view, getString(R.string.no_server_configuration),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(R.string.settings) {
                val intent = Intent(this@ScrollingActivity, SettingsActivity::class.java)
                startActivity(intent)
            }.show()
        }

        Timer(false).schedule(30000, 30000) {
            runOnUiThread { widgetsListViewAdapter.notifyDataSetChanged() }
        }
    }

    override fun onResume() {
        widgetsListViewAdapter.notifyDataSetChanged()
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