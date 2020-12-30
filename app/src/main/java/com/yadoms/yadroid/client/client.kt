package com.yadoms.yadroid.client

import io.ktor.client.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking

class Client(val baseUrl: String, val basicAuthentUser: String, val basicAuthentPassword: String) {
    private val client = HttpClient {
        expectSuccess = false
//TODO remettre
//        Auth { basic {
//            username = basicAuthentUser
//            password = basicAuthentPassword
//            sendWithoutRequest = true //TODO utile ?
//        } }
//        Logging(logger = Logger.DEFAULT, level = LogLevel.ALL)
    }

    fun get(
        url: String,
        params: String? = null,
        onOk: (String) -> Unit
    ) {
        runBlocking {
            val out = client.get<String>(baseUrl + url)
            onOk(out)
        }
    }
}