package com.yadoms.myyadoms.yadomsApi

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Helpers {
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
}