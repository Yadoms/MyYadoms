package com.yadoms.yadroid.yadomsApi

import android.content.Context
import android.util.Log
import com.beust.klaxon.Klaxon //TODO à remplacer par Moshi
import com.beust.klaxon.KlaxonException
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.json.JSONArray
import org.json.JSONObject
import java.io.StringReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DeviceApi(private val yApi: YadomsApi) {
    private val _logTag = javaClass.canonicalName

    enum class KeywordTypes(val yadomsApiKey: String) {
        NoData("nodata"),
        StringType("string"),
        Numeric("numeric"),
        Bool("bool"),
        Json("json"),
        Enum("enum"),
        DateTime("datetime")
    }

    enum class StandardCapacities(val yadomsApiKey: String) {
        Alarm("alarm"),
        ApparentPower("apparentpower"),
        ArmingAlarm("armingAlarm"),
        BatteryLevel("batteryLevel"),
        CameraMove("cameraMove"),
        ColorRGB("colorrgb"),
        ColorRGBW("colorrgbw"),
        Counter("count"),
        Current("current"),
        Curtain("curtain"),
        DateTime("datetime"),
        Debit("debit"),
        Dimmable("dimmable"),
        Direction("direction"),
        Distance("distance"),
        Duration("duration"),
        ElectricLoad("electricLoad"),
        Energy("energy"),
        Event("event"),
        Frequency("frequency"),
        Humidity("humidity"),
        Illumination("illumination"),
        IlluminationWm2("illuminationWm2"),
        Load("load"),
        Message("message"),
        PluginState("pluginState"),
        Power("power"),
        PowerFactor("powerFactor"),
        Pressure("pressure"),
        Rain("rain"),
        RainRate("rainrate"),
        Rssi("rssi"),
        SignalLevel("signalLevel"),
        SignalPower("signalPower"),
        Speed("speed"),
        Switch("switch"),
        Tamper("tamper"),
        Temperature("temperature"),
        Text("text"),
        UpDownStop("upDownStop"),
        UserCode("userCode"),
        Uv("uv"),
        Voltage("voltage"),
        Volume("volume"),
        WeatherCondition("weathercondition"),
        Weight("weight")
    }

    enum class KeywordAccess(val yadomsApiKey: String) {
        NoAccess("noaccess"),
        Get("get"),
        GetSet("getset")
    }

    data class Device(
        val id: Int,
        val pluginId: Int,
        val friendlyName: String
    )

    data class Keyword(
        val id: Int,
        val deviceId: Int,
        val friendlyName: String,
        val lastAcquisitionValue: String,
        val lastAcquisitionDate: LocalDateTime,
        val accessMode: KeywordAccess,
        val type: KeywordTypes
    )

    fun getDeviceMatchKeywordCriteria(
        context: Context?,
        expectedKeywordType: Array<KeywordTypes> = arrayOf(),
        expectedCapacity: Array<StandardCapacities> = arrayOf(),
        expectedKeywordAccess: Array<KeywordAccess> = arrayOf(),
        onOk: (List<Device>, List<Keyword>) -> Unit,
        onError: (String?) -> Unit,
    ) {
        val body = JSONObject()
        if (expectedKeywordType.isNotEmpty())
            body.put("expectedKeywordType", JSONArray(expectedKeywordType))
        if (expectedCapacity.isNotEmpty())
            body.put("expectedCapacity", JSONArray(expectedCapacity.map { it.yadomsApiKey }))
        if (expectedKeywordAccess.isNotEmpty())
            body.put("expectedKeywordAccess", JSONArray(expectedKeywordAccess.map { it.yadomsApiKey }))

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
                Log.e(_logTag, "Error sending request ($it)")//TODO gérer les erreurs dans la fonction post
                onError(it)
            }
        )
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
            context,
            url = "/device/matchcapacitytype/$expectedKeywordAccess/$expectedKeywordType",
            onOk = {
                try {
                    val jsonAdapter: JsonAdapter<GetDeviceWithCapacityTypeResultAdapter> = moshi.adapter(GetDeviceWithCapacityTypeResultAdapter::class.java)
                    val result: GetDeviceWithCapacityTypeResultAdapter? = jsonAdapter.fromJson(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk(result.data.device)
                    }
                } catch (e: JsonDataException) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            },
            onError = {
                Log.e(_logTag, "Error sending request ($it)")//TODO gérer les erreurs dans la fonction post
                onError(it)
            }
        )
    }

    class GetDeviceKeywordsResultAdapter(val result: Boolean, val message: String, val data: Data) {
        class Data(val keyword: List<Keyword>)
    }

    fun getDeviceKeywords(
        context: Context?,
        deviceId: Int,
        onOk: (List<Keyword>) -> Unit,
        onError: (String?) -> Unit,
    ) {
        yApi.get(
            context,
            url = "/device/$deviceId/keyword",
            onOk = {
                try {
                    val jsonAdapter: JsonAdapter<GetDeviceKeywordsResultAdapter> = moshi.adapter(GetDeviceKeywordsResultAdapter::class.java)
                    val result: GetDeviceKeywordsResultAdapter? = jsonAdapter.fromJson(it)

                    if (result?.result != true) {
                        Log.e(_logTag, "Server returns error (${result?.message}) :")//TODO gérer les erreurs dans la fonction post
                        onError(result?.message)
                    } else {
                        onOk(result.data.keyword)
                    }
                } catch (e: JsonDataException) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction post
                    Log.e(_logTag, it)
                    onError(null)
                }
            },
            onError = {
                Log.e(_logTag, "Error sending request ($it)")//TODO gérer les erreurs dans la fonction post
                onError(it)
            }
        )
    }

    fun getKeyword(
        context: Context?,
        keywordId: Int,
        onOk: (Keyword) -> Unit,
        onError: (String?) -> Unit,
    ) {
        yApi.get(
            context,
            url = "/device/keyword/$keywordId",
            onOk = {
                try {
                    val klaxon = Klaxon()
                    val json = klaxon.parseJsonObject(StringReader(it))

                    if (json.boolean("result") != true) {
                        Log.e(_logTag, "Server returns error (${json.string("message")}) :")//TODO gérer les erreurs dans la fonction post
                        onError(json.string("message"))
                    } else {
                        val keyword = json.obj("data")?.let { dataNode -> klaxon.parseFromJsonObject<Keyword>(dataNode) }
                        if (keyword == null) {
                            Log.e(_logTag, "Server returns error (${json.string("message")}) :")//TODO gérer les erreurs dans la fonction post
                            onError(json.string("message"))
                        } else {
                            onOk(keyword)
                        }
                    }
                } catch (e: KlaxonException) {
                    Log.e(_logTag, "Unable to parse JSON answer ($e) :")//TODO gérer les erreurs dans la fonction get
                    Log.e(_logTag, it)
                    onError(null)
                }
            },
            onError = {
                Log.e(_logTag, "Error sending request ($it) :")//TODO gérer les erreurs dans la fonction get
                onError(it)
            }
        )
    }

    fun command(
        context: Context?,
        keywordId: Int,
        command: String,
        onOk: () -> Unit,
        onError: (String?) -> Unit
    ) {
        yApi.post(
            context,
            url = "/device/keyword/${keywordId}/command",
            body = command,
            onOk = {
                try {
                    val klaxon = Klaxon()
                    val json = klaxon.parseJsonObject(StringReader(it))

                    if (json.boolean("result") != true) {
                        Log.e(_logTag, "Server returns error (${json.string("message")}) :")//TODO gérer les erreurs dans la fonction post
                        onError(json.string("message"))
                    } else {
                        onOk()
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

    internal class LocalDateTimeAdapter {
        private val pattern = "yyyyMMdd'T'HHmmss[.SSSSSS]"

        @ToJson
        fun toJson(dt: LocalDateTime) = dt.format(DateTimeFormatter.ofPattern(pattern))

        @FromJson
        fun fromJson(dt: String) = LocalDateTime.parse(dt, DateTimeFormatter.ofPattern(pattern))
    }

    companion object {
        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(LocalDateTimeAdapter())
            .build()
    }
}