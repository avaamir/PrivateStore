package com.amir.ir.privatestore.repository.persistance.cartdb

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amir.ir.privatestore.models.CartItem

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItem)

    @Update
    suspend fun update(item: CartItem)

    @Delete
    suspend fun delete(item: CartItem)

    @Query("DELETE FROM cart_tb")
    suspend fun deleteAllCartItem()

    @get:Query("SELECT * FROM cart_tb order by id DESC")
    val allCartItem: LiveData<List<CartItem>>

    @Query("SELECT * FROM cart_tb WHERE pid = :pid AND sizeId = :sizeId AND colorId = :colorId ")
    suspend fun exists(pid: Int, sizeId: Int, colorId: Int): CartItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CartItem>)

    //@Query("UPDATE items SET is_processing=1 WHERE item_id in (:items) ")
    /*@Query("UPDATE cart_tb SET count = :count, totalCount = :totalCount,  discountPrice = :discountPrice, mainPrice = :mainPrice WHERE pid = :pid AND sizeId = :sizeId AND colorId = :colorId")
    suspend fun updateSummery(
        pid: Int,
        sizeId: Int,
        colorId: Int,
        mainPrice: String,
        discountPrice: String,
        totalCount: Int,
        count: Int
    )*/

}