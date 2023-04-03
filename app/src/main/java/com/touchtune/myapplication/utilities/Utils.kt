package com.touchtune.myapplication.utilities

import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object Utils {
    @JvmStatic
    fun javaDateTimeConverter(dateTimeString: String): LocalDateTime {
        val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
        val zonedDateTime = ZonedDateTime.parse(dateTimeString, formatter)
        return LocalDateTime.from(zonedDateTime)
    }
}