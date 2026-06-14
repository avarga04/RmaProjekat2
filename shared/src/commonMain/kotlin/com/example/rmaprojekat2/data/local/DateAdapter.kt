package com.example.rmaprojekat2.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate

class DateAdapter {
    @TypeConverter
    fun serialize(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun deserialize(value: String?): LocalDate? = value?.let(LocalDate::parse)
}