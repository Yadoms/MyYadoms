package com.yadoms.myyadoms.yadomsApi

import android.util.Log
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class SystemApi(private val yApi: YadomsApi) {
    private val _logTag = javaClass.canonicalName

    class GetCurrentTimeResultAdapter(val result: Boolean, val message: String, val data: Data) {
        class Data(val now: LocalDateTime)
    }

    fun getServerTime(
        onOk: (LocalDateTime) -> Unit,
        onError: (String?) -> Unit,
    ) {
        yApi.get(
            url = "/system/currentTime",
            onOk = {
                try {
                    val result = yApi.fromJson<GetCurrentTimeResultAdapter>(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk(result.data.now)
                    }
                } catch (e: Exception) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            }
        ) {
            Log.e(_logTag, "Error sending request ($it) :")//TODO gérer les erreurs dans la fonction get
            onError(it)
        }
    }
}