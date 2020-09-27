package com.amir.ir.privatestore.repository.persistance.addressdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.models.CartItem


@Database(entities = [Address::class], version = 6, exportSchema = false)
abstract class AddressDatabase  : RoomDatabase() {

    abstract fun getDao(): AddressDao

    companion object {
        @Volatile
        private var INSTANCE: AddressDatabase? = null


        fun getInstance(context: Context): AddressDatabase =
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
                AddressDatabase::class.java, "address.db"
            ).fallbackToDestructiveMigration().build()
    }
}