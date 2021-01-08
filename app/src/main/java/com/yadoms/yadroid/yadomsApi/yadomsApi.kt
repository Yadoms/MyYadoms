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
import java.lang.Exception


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

    private fun post(
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

    data class Device(val id: Int, val pluginId: Int, val name: String, val friendlyName: String)

    fun getDeviceMatchKeywordCriteria(
        context: Context?,
        expectedKeywordType: Array<String>? = null,
        expectedCapacity: Array<String>? = null,
        expectedKeywordAccess: Array<String>? = null,
        onOk: (Array<Device>) -> Unit,
        onError: (String?) -> Unit,
    ) {
        val body = JSONObject()
        if (expectedKeywordType != null)
            body.put("expectedKeywordType", JSONArray(expectedKeywordType))
        if (expectedCapacity != null)
            body.put("expectedCapacity", JSONArray(expectedCapacity))
        if (expectedKeywordAccess != null)
            body.put("expectedKeywordAccess", JSONArray(expectedKeywordAccess))

        post(
            context,
            url = "/device/matchkeywordcriteria",
            body = body.toString(),
            onOk = {
                try {
                    val klaxon = Klaxon()
                    val json = klaxon.parseJsonObject(StringReader(it))

                    if (json.boolean("result") != true)
                        onError(json.string("message"))
                    else {
                        val devicesNodes = json.obj("data")?.array<Any>("devices")
                        val devices = devicesNodes?.let { deviceNode ->
                            klaxon.parseFromJsonArray<Device>(deviceNode)
                        }

                        onOk(devices?.toTypedArray() ?: arrayOf())
                    }
                }
                catch (e: KlaxonException)
                {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")
                    Log.e(_logTag, it)
                    onError(null)
                }
            },
            onError = { onError(it) }
        )
    }
}