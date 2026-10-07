package com.example.qrcheckin.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {

        return database ?: synchronized(this) {

            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "qr_checkin_database"
            ).build().also {
                database = it
            }
        }
    }
}