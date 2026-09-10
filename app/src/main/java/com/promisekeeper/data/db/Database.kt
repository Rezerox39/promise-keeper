package com.promisekeeper.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CompanionEntity::class, PromiseEntity::class, CheckInEntity::class, EventEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PromiseKeeperDatabase : RoomDatabase() {
    abstract fun dao(): PromiseKeeperDao

    companion object {
        @Volatile
        private var INSTANCE: PromiseKeeperDatabase? = null

        fun getInstance(context: Context): PromiseKeeperDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PromiseKeeperDatabase::class.java,
                    "promise_keeper.db"
                ).build().also { INSTANCE = it }
            }
    }
}
