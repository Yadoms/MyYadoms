package com.yadoms.yadroid

import android.util.Log
import com.yadoms.yadroid.client.Client
import java.util.*


object DevicesContent {

    /**
     * An array of sample (dummy) items.
     */
    val DEVICES: MutableList<DeviceItem> = ArrayList()

    init {
        Client("http://10.0.2.2:8080/rest", "", "").get("/device", null) {
            Log.d("testValence", it)
        }
        addItem(DeviceItem("Device 1", 1))
        addItem(DeviceItem("Device 2", 2))
    }

    private fun addItem(item: DeviceItem) = DEVICES.add(item)

    /**
     * The device item
     */
    data class DeviceItem(val friendlyName: String, val id: Int) {
        override fun toString(): String = friendlyName
    }
}