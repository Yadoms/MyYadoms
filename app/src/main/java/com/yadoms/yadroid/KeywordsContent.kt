package com.yadoms.yadroid

import java.util.*


object KeywordsContent {

    /**
     * An array of sample (dummy) items.
     */
    val KEYWORDS: MutableList<KeywordItem> = ArrayList()

    init {
        addItem(KeywordItem("Keyword 1", 1))
        addItem(KeywordItem("Keyword 2", 2))
    }

    private fun addItem(item: KeywordItem) = KEYWORDS.add(item)

    /**
     * The keyword item
     */
    data class KeywordItem(val friendlyName: String, val id: Int) {
        override fun toString(): String = friendlyName
    }
}