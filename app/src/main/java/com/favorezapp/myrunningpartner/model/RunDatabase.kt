package com.favorezapp.myrunningpartner.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

const val DB_VERSION = 1

@Database(
    entities = [Run::class],
    version = DB_VERSION
)
@TypeConverters(Converters::class)
abstract class RunDatabase: RoomDatabase() {
    abstract fun runDao(): RunDao
}