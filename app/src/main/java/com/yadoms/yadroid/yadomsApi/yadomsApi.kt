package com.yadoms.yadroid.yadomsApi

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import org.json.JSONArray
import org.json.JSONObject
import java.io.StringReader


class YadomsApi(
    val baseUrl: String,
    val basicAuthentUser: String,
    val basicAuthentPassword: String
) {
    private val _logTag = javaClass.canonicalName

    private fun get(
        url: String,
        params: String? = null,
        onOk: (String) -> Unit
    ) {
        TODO()
    }

    fun post(
        context: Context?,
        url: String,
        params: String? = null,
        body: String,
        onOk: (String) -> Unit,
        onError: (String?) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(
            Method.POST,
            baseUrl + url,
            {
                onOk(it)
            },
            {
                onError(it.message)
            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                return body.toByteArray()
            }
        }

        queue.add(stringRequest)
    }
}