package com.amir.ir.privatestore.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tb")
data class Favorite (
    @PrimaryKey
    val pid: Int,
    val userId: Int,
    val name: String,
    val image: String?
)