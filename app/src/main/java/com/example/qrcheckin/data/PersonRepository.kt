package com.example.qrcheckin.data

import android.content.Context

object PersonRepository {

    private lateinit var dao: PersonDao

    fun initialize(context: Context) {
        dao = DatabaseProvider
            .getDatabase(context)
            .personDao()
    }

    suspend fun setPeople(list: List<Person>) {
        dao.deleteAll()
        dao.insertAll(list)
    }

    suspend fun getPeople(): List<Person> {
        return dao.getAllPeople()
    }

    suspend fun findById(id: String): Person? {
        return dao.getPersonById(id)
    }

    suspend fun markScanned(id: String): CheckInResult {

        val person = dao.getPersonById(id)

        if (person == null) {
            return CheckInResult.NOT_FOUND
        }

        if (person.scanned) {
            return CheckInResult.ALREADY_SCANNED
        }

        dao.updatePerson(
            person.copy(scanned = true)
        )

        return CheckInResult.SUCCESS
    }

    suspend fun refresh(id: String): Person? {
        return dao.getPersonById(id)
    }
}