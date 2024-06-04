package com.yadoms.myyadoms

import java.time.LocalDateTime
import java.time.ZoneOffset

class ServerTime(private val currentTimeProvider: () -> LocalDateTime = { LocalDateTime.now() }) {
    private var _serverVsLocalTimeDelta: Long? = null

    public fun synchronize(serverTime: LocalDateTime) {
        _serverVsLocalTimeDelta = currentTimeProvider().toInstant(ZoneOffset.UTC).epochSecond - serverTime.toInstant(ZoneOffset.UTC).epochSecond
    }

    fun now(): LocalDateTime? {
        val delta = _serverVsLocalTimeDelta ?: return null
        val currentInstant = currentTimeProvider().toInstant(ZoneOffset.UTC)
        return LocalDateTime.ofEpochSecond(currentInstant.epochSecond - delta, 0, ZoneOffset.UTC)
    }
}
