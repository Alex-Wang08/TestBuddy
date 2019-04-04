package com.example.testbuddy.deeplink.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DeeplinkModel::class], version = 1)
abstract class DeepLinkDatabase : RoomDatabase() {

    companion object {
        private const val DEEP_LINK_DATABASE_NAME = "deep_link_db"

        private var INSTANCE: DeepLinkDatabase? = null

        private fun create(context: Context) =
            Room
                .databaseBuilder(context.applicationContext, DeepLinkDatabase::class.java, DEEP_LINK_DATABASE_NAME)
                .build()


        @Synchronized
        operator fun get(context: Context): DeepLinkDatabase = INSTANCE ?: create(context)
    }

    abstract fun deepLinkDao(): DeepLinkDao
}