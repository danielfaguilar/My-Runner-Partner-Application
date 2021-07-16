package com.favorezapp.myrunningpartner.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

const val DB_VERSION = 1
const val DB_NAME = "run_database"

@Database(
    entities = [Run::class],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RunDatabase: RoomDatabase() {
    abstract fun runDao(): RunDao
}