package com.example.qrcheckin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PersonDao {

    @Query("SELECT * FROM people WHERE id = :id LIMIT 1")
    suspend fun getPersonById(id: String): Person?

    @Query("SELECT * FROM people")
    suspend fun getAllPeople(): List<Person>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(people: List<Person>)

    @Update
    suspend fun updatePerson(person: Person)

    @Query("DELETE FROM people")
    suspend fun deleteAll()
}