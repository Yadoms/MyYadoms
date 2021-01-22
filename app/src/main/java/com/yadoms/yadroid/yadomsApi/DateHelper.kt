package com.yadoms.yadroid.yadomsApi

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateHelper {
    companion object {
        fun dateTimeFromApi(apiValue: String): LocalDateTime = //TODO utile ?
            LocalDateTime.parse(apiValue, DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss[.SSSSSS]"))
    }
}