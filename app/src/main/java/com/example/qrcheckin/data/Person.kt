package com.example.qrcheckin.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "people")
data class Person(
    @PrimaryKey
    val id: String,
    val name: String,
    val scanned: Boolean = false,
    val dinner: Boolean,
    val type: PersonType,
    val members: Int
)