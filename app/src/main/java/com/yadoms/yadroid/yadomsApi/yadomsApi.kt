package com.yadoms.yadroid.yadomsApi

import android.util.Log
import com.beust.klaxon.Klaxon
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking

class YadomsApi(
    val baseUrl: String,
    val basicAuthentUser: String,
    val basicAuthentPassword: String
) {
    private val client = HttpClient {
        expectSuccess = false
        install(HttpTimeout) {
            requestTimeoutMillis = 2000 //TODO mettre à 5000
        }
//TODO remettre
//        Auth { basic {
//            username = basicAuthentUser
//            password = basicAuthentPassword
//            sendWithoutRequest = true //TODO utile ?
//        } }
//        Logging(logger = Logger.DEFAULT, level = LogLevel.ALL)
    }

    private fun get(
        url: String,
        params: String? = null,
        onOk: (String) -> Unit
    ) {
        runBlocking {
            try {
                val out = client.get<String>(baseUrl + url)
                onOk(out)
            } catch (exception: HttpRequestTimeoutException) {
                Log.w("yadomsApi", "Timeout")

            }
        }
    }

    private fun post(
        url: String,
        params: String? = null,
        body: String,
        onOk: (String) -> Unit,
        onError: () -> Unit
    ) {
        runBlocking {
            try {
                val out = client.post<HttpResponse>(baseUrl + url, body = body)
                onOk(out.toString())
            } catch (exception:Throwable) {
                onError()
            }
        }
    }

    fun getDeviceMatchKeywordCriteria(
        expectedKeywordType: Array<String>? = null,
        expectedCapacity: Array<String>? = null,
        expectedKeywordAccess: Array<String>? = null,
        onOk: (String) -> Unit,
        onError: () -> Unit,
    ) {
        data class Body(
            val expectedKeywordType: Array<String>? = null,
            val expectedCapacity: Array<String>? = null,
            val expectedKeywordAccess: Array<String>? = null
        )

        val body = Klaxon().toJsonString(Body(expectedKeywordType, expectedCapacity, expectedKeywordAccess))


        post(
            url = "/device/matchkeywordcriteria",
            body = body,
            onOk = {onOk(it)},
            onError = {onError()}
        )


    }
}