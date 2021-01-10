package com.yadoms.yadroid.yadomsApi

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class YadomsApi(
    private val appPreferences: SharedPreferences,
) {
    private val _logTag = javaClass.canonicalName
    private val baseUrl:String

    init {
        val url = appPreferences.getString("server_url", "")
        val port = appPreferences.getString("server_port", "8080")
        baseUrl = "http://$url:$port/rest"
    }

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