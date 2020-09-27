package com.amir.ir.privatestore.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @SerializedName("sender")
    val sender: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("sender_image")
    val senderImage: String?
) : Parcelable