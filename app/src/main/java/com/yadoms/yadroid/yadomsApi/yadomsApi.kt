package com.yadoms.yadroid.yadomsApi

import android.content.Context
import android.util.Base64
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.yadoms.yadroid.preferences.Preferences
import org.json.JSONException
import java.io.UnsupportedEncodingException


//TODO remplacer Volley par https://square.github.io/retrofit/

class YadomsApi(private val serverConnection: Preferences.ServerConnection) {
    private val baseUrl: String
    private val commonHeaders = buildCommonHeaders()

    private fun buildCommonHeaders(): MutableMap<String, String> {
        val headers = buildAuthHeaders().toMutableMap()
        headers["Content-Type"] = "application/json;charset=UTF-8"
        return headers
    }

    private fun buildAuthHeaders(): Map<String, String> {
        if (!serverConnection.useBasicAuthentication)
            return emptyMap()

        val headers: MutableMap<String, String> = HashMap()
        val credentials = "${serverConnection.basicAuthenticationUser}:${serverConnection.basicAuthenticationPassword}"
        headers["Authorization"] = ("Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP))
        return headers
    }

    init {
        val protocol = if (serverConnection.useHttps) "https" else "http"
        val port = if (serverConnection.useHttps) serverConnection.httpsPort else serverConnection.port
        baseUrl = "$protocol://${serverConnection.url}:$port/rest"
    }

    fun get(
        context: Context?,
        url: String,
        params: MutableMap<String, String> = mutableMapOf(),
        onOk: (String) -> Unit,
        onError: (String?) -> Unit
    ) {
        val queue = Volley.newRequestQueue(context)

        var urlWithParam = url

        //TODO utiliser un vrai URI builder (pour gérer les espaces et autres caractères spéciaux)
        // Volley doesn't support params for GET request (getParams won't be called)
        // So provide param by constructing URL
        if (params.isNotEmpty()) {
            var delimiter = '?'
            params.forEach {
                urlWithParam += delimiter + it.key
                if (it.value.isNotEmpty())
                    urlWithParam += '=' + it.value
                delimiter = '&'
            }
        }

        val stringRequest = object : StringRequest(
            Method.GET,
            baseUrl + urlWithParam,
            {
                onOk(it)
            },
            {
                onError(it.message)
            }) {
            override fun getHeaders(): Map<String, String> {
                return commonHeaders + super.getHeaders()
            }

            override fun parseNetworkResponse(response: NetworkResponse?): Response<String> {
                return try {
                    // Some old Yadoms versions doesn't set "utf-8" charset in header, even if UTF-8 data is sent
                    // So force UTF-8 charset
                    Response.success(
                        response?.data?.toString(Charsets.UTF_8),
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
                } catch (e: UnsupportedEncodingException) {
                    Response.error(ParseError(e))
                } catch (je: JSONException) {
                    Response.error(ParseError(je))
                }
            }
        }

        queue.add(stringRequest)
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
                return commonHeaders + super.getHeaders()
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
}