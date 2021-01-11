package com.yadoms.yadroid.yadomsApi

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class YadomsApi(private val appPreferences: SharedPreferences) {
    private val baseUrl: String

    init {
        val url = appPreferences.getString("server_url", "")
        val useHttps = appPreferences.getBoolean("server_use_https", false)
        val protocol = if (useHttps) "https" else "http"
        val port = if (useHttps) appPreferences.getString(
            "server_https_port",
            "443"
        ) else appPreferences.getString("server_port", "8080")
        baseUrl = "$protocol://$url:$port/rest"
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
            override fun getHeaders(): Map<String, String> {
                val headers = buildAuthHeaders().toMutableMap()
                headers["Content-Type"] = "application/json;charset=UTF-8"
                return headers + super.getHeaders()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=$paramsEncoding"
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                return body.toByteArray()
            }
        }

        queue.add(stringRequest)
    }

    private fun buildAuthHeaders(): Map<String, String> {
        if (!appPreferences.getBoolean("server_use_basic_authentication", false))
            return emptyMap()

        val user = appPreferences.getString("server_basic_authentication_username", "")
        val password = appPreferences.getString("server_basic_authentication_password", "")

        val headers: MutableMap<String, String> = HashMap()
        val credentials = "$user:$password"
        headers["Authorization"] = ("Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP))
        return headers
    }
}