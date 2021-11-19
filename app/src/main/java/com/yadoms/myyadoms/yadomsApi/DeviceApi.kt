package com.yadoms.myyadoms.yadomsApi

import android.content.Context
import android.util.Log
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DeviceApi(private val yApi: YadomsApi) {
    private val _logTag = javaClass.canonicalName

    enum class KeywordTypes {
        NoData,
        StringType,
        Numeric,
        Bool,
        Json,
        Enum,
        DateTime
    }

    enum class StandardCapacities {
        alarm,
        apparentPower,
        armingAlarm,
        batteryLevel,
        cameraMove,
        colorrgb,
        colorrgbw,
        count,
        current,
        curtain,
        dateTime,
        debit,
        dimmable,
        direction,
        length,
        duration,
        electricLoad,
        energy,
        event,
        frequency,
        humidity,
        illumination,
        illuminationWm2,
        load,
        message,
        power,
        powerFactor,
        pressure,
        rain,
        rainrate,
        rssi,
        signalLevel,
        signalPower,
        speed,
        switch,
        tamper,
        temperature,
        text,
        upDownStop,
        userCode,
        uv,
        voltage,
        volume,
        weatherCondition,
        weight,
        pluginState,
        pluginStateMessage,
        deviceState,
        deviceStateMessage,
    }

    enum class Units(val yadomsApiKey: String, val label: String) {
        NoUnit("data.units.noUnit", ""),
        Ampere("data.units.ampere", "A"),
        AmperePerHOur("data.units.ampereHour", "Ah"),
        CubicMetre("data.units.cubicMetre", "m³"),
        CubicMeterPerSecond("data.units.cubicMeterPerSecond", "m³/s"),
        Decibel("data.units.decibel", "dB"),
        DecibelPerMilliWatt("data.units.decibelPerMilliWatt", "dBm"),
        Degrees("data.units.degrees", "°"),
        DegreesCelcius("data.units.degreesCelcius", "°C"),
        DegreesFarenheit("data.units.degreesFarenheit", "°F"),
        HectoPascal("data.units.hectoPascal", "hPa"),
        Hertz("data.units.hertz", "Hz"),
        Kg("data.units.kg", "Kg"),
        Lux("data.units.lux", "lux"),
        Meter("data.units.meter", "m"),
        MetersPerSecond("data.units.metersPerSecond", "m/s"),
        Millimeter("data.units.millimeter", "mm"),
        MillimeterPerSecond("data.units.millimeterPerSecond", "mm/s"),
        Percent("data.units.percent", "%"),
        Second("data.units.second", "s"),
        Uv("data.units.uv", "UV"),
        Volt("data.units.volt", "V"),
        VoltAmpere("data.units.voltAmpere", "VA"),
        Watt("data.units.watt", "W"),
        WattPerHour("data.units.wattPerHour", "Wh"),
        WattPerSquareMeter("data.units.wattPerSquareMeter", "W/m²");
    }

    enum class KeywordAccess {
        NoAccess,
        Get,
        GetSet
    }

    data class Device(
        val id: Int,
        val pluginId: Int,
        val friendlyName: String
    )

    data class Keyword(
        val id: Int,
        val deviceId: Int,
        val capacityName: StandardCapacities,
        val friendlyName: String,
        val lastAcquisitionValue: String,
        val lastAcquisitionDate: LocalDateTime?,
        val accessMode: KeywordAccess,
        val type: KeywordTypes,
        val units: Units
    )

    class GetDeviceMatchKeywordCriteriaRequestAdapter(
        val expectedKeywordType: Array<KeywordTypes>,
        val expectedCapacity: Array<StandardCapacities>,
        val expectedKeywordAccess: Array<KeywordAccess>
    )

    class GetDeviceMatchKeywordCriteriaResultAdapter(val result: Boolean, val message: String, val data: Data) {
        class Data(val devices: List<Device>, val keywords: List<Keyword>)
    }

    private inline fun <reified T> fromJson(json: String): T? {
        return try {
            moshi.adapter(T::class.java).fromJson(json)
        } catch (e: java.io.EOFException) {
            Log.w(_logTag, "Sometimes last char is not received (on emulator), so add '}' and retry parse received data...");
            moshi.adapter(T::class.java).fromJson("$json}")
        }
    }

    fun getDeviceMatchKeywordCriteria(
        expectedKeywordType: Array<KeywordTypes> = arrayOf(),
        expectedCapacity: Array<StandardCapacities> = arrayOf(),
        expectedKeywordAccess: Array<KeywordAccess> = arrayOf(),
        onOk: (List<Device>, List<Keyword>) -> Unit,
        onError: (String?) -> Unit,
    ) {
        val body = moshi.adapter(GetDeviceMatchKeywordCriteriaRequestAdapter::class.java)
            .toJson(GetDeviceMatchKeywordCriteriaRequestAdapter(expectedKeywordType, expectedCapacity, expectedKeywordAccess))

        yApi.post(
            url = "/device/matchkeywordcriteria",
            body = body.toString(),
            onOk = {
                try {
                    val result = fromJson<GetDeviceMatchKeywordCriteriaResultAdapter>(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk(result.data.devices, result.data.keywords)
                    }
                } catch (e: Exception) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            }
        ) {
            Log.e(_logTag, "Error sending request ($it)")//TODO gérer les erreurs dans la fonction post
            onError(it)
        }
    }

    class GetDeviceWithCapacityTypeResultAdapter(val result: Boolean, val message: String, val data: Data) {
        class Data(val device: List<Device>)
    }

    fun getDeviceWithCapacityType(
        context: Context?,
        expectedKeywordType: KeywordTypes,
        expectedKeywordAccess: KeywordAccess = KeywordAccess.NoAccess,
        onOk: (List<Device>) -> Unit,
        onError: (String?) -> Unit,
    ) {
        yApi.get(
            url = "/device/matchcapacitytype/$expectedKeywordAccess/$expectedKeywordType",
            onOk = {
                try {
                    val result = fromJson<GetDeviceWithCapacityTypeResultAdapter>(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk(result.data.device)
                    }
                } catch (e: Exception) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            }
        ) {
            Log.e(_logTag, "Error sending request ($it)")//TODO gérer les erreurs dans la fonction post
            onError(it)
        }
    }

    class GetDeviceKeywordsResultAdapter(val result: Boolean, val message: String, val data: Data) {
        class Data(val keyword: List<Keyword>)
    }

    fun getDeviceKeywords(
        deviceId: Int,
        onOk: (List<Keyword>) -> Unit,
        onError: (String?) -> Unit,
    ) {
        yApi.get(
            url = "/device/$deviceId/keyword",
            onOk = {
                try {
                    val result = fromJson<GetDeviceKeywordsResultAdapter>(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk(result.data.keyword)
                    }
                } catch (e: Exception) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            }
        ) {
            Log.e(_logTag, "Error sending request ($it)")//TODO gérer les erreurs dans la fonction post
            onError(it)
        }
    }

    class GetKeywordResultAdapter(val result: Boolean, val message: String, val data: Keyword)

    fun getKeyword(
        keywordId: Int,
        onOk: (Keyword) -> Unit,
        onError: (String?) -> Unit,
    ) {
        yApi.get(
            url = "/device/keyword/$keywordId",
            onOk = {
                try {
                    val result = fromJson<GetKeywordResultAdapter>(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk(result.data)
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

    class CommandResultAdapter(val result: Boolean, val message: String)

    fun command(
        keywordId: Int,
        command: String,
        onOk: () -> Unit,
        onError: (String?) -> Unit
    ) {
        yApi.post(
            url = "/device/keyword/${keywordId}/command",
            body = command,
            onOk = {
                try {
                    val result = fromJson<CommandResultAdapter>(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk()
                    }
                } catch (e: Exception) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            }
        ) {
            Log.e(_logTag, "Error sending request ($it) :")//TODO gérer les erreurs dans la fonction post
            onError(it)
        }
    }

    internal class LocalDateTimeAdapter {
        private val pattern = "yyyyMMdd'T'HHmmss[.SSSSSS]"
        private val notADateTimeSpecialValue = "not-a-date-time"

        @ToJson
        fun toJson(dt: LocalDateTime?): String? = when (dt) {
            null -> notADateTimeSpecialValue
            else -> dt.format(DateTimeFormatter.ofPattern(pattern))
        }

        @FromJson
        fun fromJson(dt: String): LocalDateTime? = when (dt) {
            notADateTimeSpecialValue -> null
            else -> LocalDateTime.parse(dt, DateTimeFormatter.ofPattern(pattern))
        }
    }

    internal class UnitsAdapter {
        @ToJson
        fun toJson(unit: Units?): String? = unit?.yadomsApiKey

        @FromJson
        fun fromJson(unit: String): Units? {
            val map = Units.values().associateBy(Units::yadomsApiKey)
            return map[unit]
        }
    }

    companion object {
        private val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(LocalDateTimeAdapter())
            .add(UnitsAdapter())
            .build()
    }
}