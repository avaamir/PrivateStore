package com.amir.ir.privatestore.repository.persistance.favoritedb

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.models.Favorite

@Dao
interface FavoriteDao {
    @get:Query("SELECT * FROM favorite_tb")
    val allFavorite: LiveData<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Favorite)

    @Update
    suspend fun update(item: Favorite)

    @Query("DELETE FROM favorite_tb WHERE pid = :pid")
    suspend fun delete(pid: Int)

    @Query("DELETE FROM favorite_tb")
    suspend fun deleteAllFavorite()

    @Query("SELECT * FROM favorite_tb WHERE pid = :pid and userId = :userId")
    suspend fun exists(pid: Int, userId: Int): Favorite?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<Favorite>)
}