package com.yadoms.yadroid.client

import android.util.Log
import io.ktor.client.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking

class Client(val baseUrl: String, val basicAuthentUser:String, val basicAuthentPassword:String) {
    private val client = HttpClient {
        expectSuccess = false
//TODO remettre
//        Auth { basic {
//            username = basicAuthentUser
//            password = basicAuthentPassword
//            sendWithoutRequest = true //TODO utile ?
//        } }
        ResponseObserver { response ->
            println("HTTP status: ${response.status.value}")
        }
        Logging(logger = Logger.DEFAULT, level = LogLevel.ALL)
    }

    fun get(
        url: String,
        params: String? = null,
//TODO        responseHandler: ResponseHandlerInterface
    ) {
        runBlocking {
            //TODO utile ? Log.d(javaClass.simpleName, "GET : " + url + ", params : " + (params ?: ""))
//            val htmlContent = request {
//                url(baseUrl+url)
//                method = HttpMethod.Get
//            }
            val out = client.get<String>(baseUrl+url)
            Log.d(javaClass.simpleName, out)
        }
    }
}