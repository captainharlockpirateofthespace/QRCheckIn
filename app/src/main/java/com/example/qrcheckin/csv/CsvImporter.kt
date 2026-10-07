package com.example.qrcheckin.csv

import android.content.Context
import android.net.Uri
import com.example.qrcheckin.data.Person
import com.example.qrcheckin.data.PersonType

class CsvImporter {

    fun import(context: Context, uri: Uri): List<Person> {

        val people = mutableListOf<Person>()

        context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { reader ->

            // Skip header
            reader.readLine()

            reader.forEachLine { line ->

                if (line.isBlank()) return@forEachLine

                val parts = line.split(",")

                if (parts.size != 5) return@forEachLine

                try {

                    val type = when (parts[3].trim().uppercase()) {
                        "INDIVIDUAL" -> PersonType.INDIVIDUAL
                        "FAMILY" -> PersonType.FAMILY
                        "GROUP" -> PersonType.GROUP
                        else -> return@forEachLine
                    }

                    people.add(
                        Person(
                            id = parts[0].trim(),
                            name = parts[1].trim(),
                            scanned = false,
                            dinner = parts[2].trim().toBooleanStrict(),
                            type = type,
                            members = parts[4].trim().toInt()
                        )
                    )

                } catch (_: Exception) {
                    // Ignore invalid rows
                }
            }
        }

        return people
    }
}