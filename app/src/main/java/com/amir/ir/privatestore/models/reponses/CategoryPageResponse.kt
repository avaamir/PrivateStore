package com.amir.ir.privatestore.models.reponses

import com.amir.ir.privatestore.models.Category
import com.amir.ir.privatestore.models.Product
import com.amir.ir.privatestore.models.Slide
import com.google.gson.annotations.SerializedName

data class CategoryPageResponse(
    val id: Int, //age az slide umade bashe nadarimesh va mikhamesh
    val title: String, //age az slide umade bashe nadarimesh va mikhamesh
    @SerializedName("slides")
    val slides: ArrayList<Slide>, //TODO badan momkene List<List<SLide>> beshe
    @SerializedName("subCategories")
    val subCategories: ArrayList<Category>,
    @SerializedName("mostSells")
    val mostSellProducts: ArrayList<Product>,
    @SerializedName("amazingProducts")   //TODO badan strategy our recommod samte server bayad tarif beshe
    val offeredProducts: ArrayList<Product>,
    @SerializedName("newest")
    val newProducts: ArrayList<Product>
)