package com.yadoms.myyadoms.yadomsApi

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yadoms.myyadoms.preferences.Preferences
import org.json.JSONException
import java.io.UnsupportedEncodingException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


//TODO remplacer Volley par https://square.github.io/retrofit/

class YadomsApi(val context: Context) {
    private val queue = Volley.newRequestQueue(context)

    private fun buildBaseUrl(serverConnection: Preferences.ServerConnection): String {
        val protocol = if (serverConnection.useHttps) "https" else "http"
        val port =
            if (serverConnection.useHttps) serverConnection.httpsPort else serverConnection.port
        return "$protocol://${serverConnection.url}:$port/rest"
    }

    private fun buildCommonHeaders(serverConnection: Preferences.ServerConnection): MutableMap<String, String> {
        val headers = buildAuthHeaders(serverConnection).toMutableMap()
        headers["Content-Type"] = "application/json;charset=UTF-8"
        return headers
    }

    private fun buildAuthHeaders(serverConnection: Preferences.ServerConnection): Map<String, String> {
        if (!serverConnection.useBasicAuthentication)
            return emptyMap()

        val headers: MutableMap<String, String> = HashMap()
        val credentials =
            "${serverConnection.basicAuthenticationUser}:${serverConnection.basicAuthenticationPassword}"
        headers["Authorization"] =
            ("Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP))
        return headers
    }

    // define the default variables for proper certificate validation
    private val defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()
    private val defaultSSLHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()

    private fun setDefaultSettingsForHttpsConnection() {
        HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier(defaultSSLHostnameVerifier)
    }

    private fun bypassSSLValidation() {
        try {
            val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(
                    certs: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(
                    certs: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }
            })
            val sc = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
        } catch (ex: NoSuchAlgorithmException) {
            ex.printStackTrace()
        } catch (ex: KeyManagementException) {
            ex.printStackTrace()
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    private fun checkAndHandleSSLHandshake(serverConnection: Preferences.ServerConnection) {
        if (serverConnection.useHttps && serverConnection.ignoreHttpsCertificateError) {
            bypassSSLValidation()
        } else {
            setDefaultSettingsForHttpsConnection()
        }
    }

    private fun addToQueue(stringRequest: StringRequest, serverConnection: Preferences.ServerConnection) {
        checkAndHandleSSLHandshake(serverConnection)
        queue.add(stringRequest)
    }

    fun get(
        url: String,
        params: MutableMap<String, String> = mutableMapOf(),
        onOk: (String) -> Unit,
        onError: (String?) -> Unit
    ) {
        var urlWithParam = url

        //TODO utiliser un vrai URI builder (UrlRequest.Builder ?) pour gérer les espaces et autres caractères spéciaux.
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

        val serverConnection = Preferences(context).serverConnection
        val stringRequest = object : StringRequest(
            Method.GET,
            buildBaseUrl(serverConnection) + urlWithParam,
            {
                onOk(it)
            },
            {
                onError(it.message)
            }) {
            override fun getHeaders(): Map<String, String> {
                return buildCommonHeaders(serverConnection) + super.getHeaders()
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

        addToQueue(stringRequest, serverConnection)
    }

    fun post(
        url: String,
        params: String? = null,
        body: String,
        onOk: (String) -> Unit,
        onError: (String?) -> Unit
    ) {
        val serverConnection = Preferences(context).serverConnection
        val stringRequest = object : StringRequest(
            Method.POST,
            buildBaseUrl(serverConnection) + url,
            {
                onOk(it)
            },
            {
                onError(it.message)
            }) {
            override fun getHeaders(): Map<String, String> {
                return buildCommonHeaders(serverConnection) + super.getHeaders()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=$paramsEncoding"
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                return body.toByteArray()
            }
        }

        addToQueue(stringRequest, serverConnection)
    }

    inline fun <reified T> fromJson(json: String): T? {
        return try {
            moshi.adapter(T::class.java).fromJson(json)
        } catch (e: java.io.EOFException) {
            // Sometimes last char is not received (on emulator), so add '}' and retry parse received data...
            moshi.adapter(T::class.java).fromJson("$json}")
        }
    }

    companion object {
        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(Helpers.LocalDateTimeAdapter())
            .add(DeviceApi.UnitsAdapter())
            .build()
    }
}