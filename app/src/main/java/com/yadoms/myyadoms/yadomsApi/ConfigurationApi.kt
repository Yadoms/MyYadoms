package com.yadoms.myyadoms.yadomsApi

import android.location.Location
import android.util.Log


class ConfigurationApi(private val yApi: YadomsApi) {
    private val _logTag = javaClass.canonicalName

    class GetCurrentTimeResultAdapter(val result: Boolean, val message: String, val data: Data) {
        class LocationData(
            val latitude: Double,
            val longitude: Double
        )

        class Data(val location: LocationData)
    }

    fun getYadomsServerLocation(
        onOk: (Location) -> Unit,
        onError: (String?) -> Unit,
    ) {
        yApi.get(
            url = "/configuration/server",
            onOk = {
                try {
                    val result = yApi.fromJson<GetCurrentTimeResultAdapter>(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")
                        onError(result?.message)
                    } else {
                        val location = Location("")
                        location.latitude = result.data.location.latitude
                        location.longitude = result.data.location.longitude
                        onOk(location)
                    }
                } catch (e: Exception) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")
                    Log.e(_logTag, it)
                    onError(null)
                }
            }
        ) {
            Log.e(_logTag, "Error sending request ($it) :")
            onError(it)
        }
    }
}