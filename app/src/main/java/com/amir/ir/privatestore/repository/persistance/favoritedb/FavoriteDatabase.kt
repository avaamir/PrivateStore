package com.amir.ir.privatestore.repository.persistance.favoritedb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amir.ir.privatestore.models.Favorite


@Database(entities = [Favorite::class], version = 4, exportSchema = false)
abstract class FavoriteDatabase  : RoomDatabase() {

    abstract fun getDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteDatabase? = null


        fun getInstance(context: Context): FavoriteDatabase =
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
                FavoriteDatabase::class.java, "favorite.db"
            ).fallbackToDestructiveMigration().build()
    }
}