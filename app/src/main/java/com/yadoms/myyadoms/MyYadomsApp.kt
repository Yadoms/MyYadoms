package com.yadoms.myyadoms

import android.app.Application
import android.util.Log
import com.yadoms.myyadoms.yadomsApi.SystemApi
import com.yadoms.myyadoms.yadomsApi.YadomsApi

// TODO gérer ici l'instance YadomsApi (une seule instance du coup) ?
class MyYadomsApp : Application() {
    private val _logTag = javaClass.canonicalName

    val serverTime: ServerTime by lazy {
        ServerTime()
    }

    override fun onCreate() {
        super.onCreate()

        SystemApi(YadomsApi(this)).getServerTime(
            onOk = { serverTime.synchronize(it) },
            onError = { Log.e(_logTag, "Unable to retrieve server time") })
    }
}