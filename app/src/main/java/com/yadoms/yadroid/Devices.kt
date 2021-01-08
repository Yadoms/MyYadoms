package com.yadoms.yadroid


object Devices {


    /**
     * The device item
     */
    data class Item(val friendlyName: String, val id: Int) {
        override fun toString(): String = friendlyName
    }
}