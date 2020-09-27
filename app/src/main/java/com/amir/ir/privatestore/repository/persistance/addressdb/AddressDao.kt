package com.amir.ir.privatestore.repository.persistance.addressdb

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amir.ir.privatestore.models.Address

@Dao
interface AddressDao {

    @get:Query("SELECT * FROM address_tb order by id DESC")
    val allAddress: LiveData<List<Address>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Address)

    @Update
    suspend fun update(item: Address)

    @Update
    suspend fun update(items: List<Address>)

    @Delete
    suspend fun delete(item: Address)

    @Delete
    suspend fun delete(item: List<Address>)

    @Query("DELETE FROM address_tb")
    suspend fun deleteAllAddress()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Address>)
}