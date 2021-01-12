package com.yadoms.yadroid.yadomsApi

import android.content.Context
import android.util.Log
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import org.json.JSONArray
import org.json.JSONObject
import java.io.StringReader

class DeviceApi(private val yApi: YadomsApi) {
    private val _logTag = javaClass.canonicalName

    data class Device(val id: Int, val pluginId: Int, val friendlyName: String)
    data class Keyword(val id: Int, val deviceId: Int, val friendlyName: String)

    fun getDeviceMatchKeywordCriteria(
        context: Context?,
        expectedKeywordType: Array<String>? = null,
        expectedCapacity: Array<String>? = null,
        expectedKeywordAccess: Array<String>? = null,
        onOk: (List<Device>, List<Keyword>) -> Unit,
        onError: (String?) -> Unit,
    ) {
        val body = JSONObject()
        if (expectedKeywordType != null)
            body.put("expectedKeywordType", JSONArray(expectedKeywordType))
        if (expectedCapacity != null)
            body.put("expectedCapacity", JSONArray(expectedCapacity))
        if (expectedKeywordAccess != null)
            body.put("expectedKeywordAccess", JSONArray(expectedKeywordAccess))

        yApi.post(
            context,
            url = "/device/matchkeywordcriteria",
            body = body.toString(),
            onOk = {
                try {
                    val klaxon = Klaxon()
                    val json = klaxon.parseJsonObject(StringReader(it))

                    if (json.boolean("result") != true) {
                        Log.e(_logTag, "Server returns error (${json.string("message")}) :")//TODO gérer les erreurs dans la fonction post
                        onError(json.string("message"))
                    } else {
                        val devicesNodes = json.obj("data")?.array<Any>("devices")
                        val devices = devicesNodes?.let { deviceNode ->
                            klaxon.parseFromJsonArray<Device>(deviceNode)
                        }
                        val keywordsNodes = json.obj("data")?.array<Any>("keywords")
                        val keywords = keywordsNodes?.let { keywordsNode ->
                            klaxon.parseFromJsonArray<Keyword>(keywordsNode)
                        }

                        onOk(devices ?: emptyList(), keywords ?: emptyList())
                    }
                } catch (e: KlaxonException) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            },
            onError = {
                Log.e(_logTag, "Error sending request ($it) :")//TODO gérer les erreurs dans la fonction post
                onError(it)
            }
        )
    }
}