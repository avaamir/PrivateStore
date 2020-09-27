package com.amir.ir.privatestore.repository.persistance.messagedb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amir.ir.privatestore.models.Message


@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class MessageDatabase  : RoomDatabase() {

    abstract fun getDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: MessageDatabase? = null


        fun getInstance(context: Context): MessageDatabase =
            INSTANCE
                ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(
                        context
                    )
                        .also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MessageDatabase::class.java, "message.db"
            ).fallbackToDestructiveMigration().build()
    }
}