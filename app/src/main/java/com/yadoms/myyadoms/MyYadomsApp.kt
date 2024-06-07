package com.yadoms.myyadoms

import android.app.Activity
import android.app.Application


// TODO gérer ici l'instance YadomsApi (une seule instance du coup) ?
class MyYadomsApp : Application() {
    private val _logTag = javaClass.canonicalName

    val serverTime = ServerTime()

}
