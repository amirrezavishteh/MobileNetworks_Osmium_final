package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.osmium.data.db.CellDao
import com.example.osmium.data.db.SignalDao

const val DB_VERSION = 1

@Database(
    entities = [
        Signal::class,
    ],
    version = DB_VERSION,
)
abstract class AppDatabase : RoomDatabase() {

    abstract val signalDao: SignalDao

    companion object {
        private const val DB_NAME = "cells.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {

            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        DB_NAME
                    ).build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
