package com.yadoms.yadroid

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yadoms.yadroid.databinding.ActivityScrollingBinding
import com.yadoms.yadroid.preferences.Preferences
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
            addWidget.setOnClickListener {
                registerForActivityResult(NewWidgetActivityContract()) { newWidget ->
                    newWidget?.let { widgetsListViewAdapter.addNewWidget(it) }
                }.launch(null)
            }
        }

        widgetsListView.layoutManager = LinearLayoutManager(this)
        widgetsListView.adapter = WidgetsRecyclerViewAdapter(Preferences(this))
        val itemTouchHelper = ItemTouchHelper(WidgetSwipeAndDragHandler(widgetsListViewAdapter))
        itemTouchHelper.attachToRecyclerView(widgetsListView)

        Timer(false).schedule(30000, 30000) {
            runOnUiThread { widgetsListViewAdapter.notifyDataSetChanged() }
        }

//        binding.contentScrollingLayout.swipeContainer.setOnRefreshListener {
//            //TODO refresh widgets
//            Log.d("test", "test")
//            sleep(500)
//            binding.contentScrollingLayout.swipeContainer.isRefreshing = false;
//        }
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