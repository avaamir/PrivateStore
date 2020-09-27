package com.amir.ir.privatestore.repository.persistance.cartdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amir.ir.privatestore.models.CartItem


@Database(entities = [CartItem::class], version = 5, exportSchema = false)
abstract class CartDatabase  : RoomDatabase() {

    abstract fun getDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: CartDatabase? = null


        fun getInstance(context: Context): CartDatabase =
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
                CartDatabase::class.java, "cart.db"
            ).fallbackToDestructiveMigration().build()
    }
}