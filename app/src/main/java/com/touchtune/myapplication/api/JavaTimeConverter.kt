package com.touchtune.myapplication.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JavaTimeConverter : JsonAdapter<LocalDateTime>() {

    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): LocalDateTime? {
        val dateString = reader.nextString()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        return LocalDateTime.parse(dateString, formatter)
    }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: LocalDateTime?) {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val dateString = value?.format(formatter)
        writer.value(dateString)
    }
}
