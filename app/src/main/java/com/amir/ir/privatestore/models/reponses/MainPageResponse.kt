package com.amir.ir.privatestore.models.reponses

import com.amir.ir.privatestore.models.Category
import com.amir.ir.privatestore.models.Product
import com.amir.ir.privatestore.models.Slide
import com.google.gson.annotations.SerializedName

data class MainPageResponse(
    @SerializedName("slideShow")
    val slides: ArrayList<Slide>,
    @SerializedName("mainCategory")
    val categories: ArrayList<Category>,
    @SerializedName("amazingProducts")
    val amazingProducts: ArrayList<Product>,
    @SerializedName("mostSells")
    val mostSellProducts: ArrayList<Product>,
    @SerializedName("newest")
    val newProducts: ArrayList<Product>,
    @SerializedName("amazing_timer")
    val timer: Long,
    @SerializedName("appUpdate")
    val appUpdateInfo: AppUpdate?
)

data class AppUpdate(
    @SerializedName("version")
    val version: String,
    @SerializedName("massage")
    val message: String,
    @SerializedName("force")
    private val _force: Int,
    @SerializedName("link")
    val link: String
) {
    val isForce get() = _force == 1
}