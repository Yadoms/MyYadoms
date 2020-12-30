package com.yadoms.yadroid

import android.util.Log
import com.yadoms.yadroid.client.Client
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import java.util.*


object DevicesContent {

    /**
     * An array of sample (dummy) items.
     */
    val DEVICES: MutableList<DeviceItem> = ArrayList()

    init {
        testKtor()
        Client ("https://fr.wikipedia.org/wiki", "", "").get("/Valence#France")
        addItem(DeviceItem("Device 1", 1))
        addItem(DeviceItem("Device 2", 2))
    }

    private fun testKtor() {
        runBlocking {
            val client = HttpClient()
            val htmlContent = client.get<String>("https://en.wikipedia.org/wiki/Main_Page")
            Log.d("testKtor", htmlContent)
        }

    }

    private fun addItem(item: DeviceItem) = DEVICES.add(item)

    /**
     * The device item
     */
    data class DeviceItem(val friendlyName: String, val id: Int) {
        override fun toString(): String = friendlyName
    }
}