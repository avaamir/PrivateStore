package com.amir.ir.privatestore.repository.persistance.userdb

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amir.ir.privatestore.models.CartItem
import com.amir.ir.privatestore.models.User

@Dao
interface UserDao {

    @get:Query("SELECT * FROM user_tb")
    val users: LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: User)

    @Update
    suspend fun update(item: User)

    @Delete
    suspend fun delete(item: User)

    @Query("SELECT * FROM user_tb WHERE _id = :id")
    suspend fun exists(id: Int): User?
}