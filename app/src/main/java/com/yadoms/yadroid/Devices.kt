package com.yadoms.yadroid

import android.util.Log
import com.yadoms.yadroid.yadomsApi.YadomsApi
import java.util.*


object Devices {

    /**
     * An array of sample (dummy) items.
     */
    val list: MutableList<Item> = ArrayList()

    init {
//        YadomsApi("http://10.0.2.2:8080/rest", "", "").get("/device", null) {
//            Log.d("testValence", it)
//        }
        addItem(Item("Device 1", 1))
        addItem(Item("Device 2", 2))
    }

    private fun addItem(item: Item) = list.add(item)

    /**
     * The device item
     */
    data class Item(val friendlyName: String, val id: Int) {
        override fun toString(): String = friendlyName
    }
}