package com.example.qrcheckin.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromPersonType(type: PersonType): String {
        return type.name
    }

    @TypeConverter
    fun toPersonType(value: String): PersonType {
        return PersonType.valueOf(value)
    }
}