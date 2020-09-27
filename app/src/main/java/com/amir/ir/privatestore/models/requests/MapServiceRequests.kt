package com.amir.ir.privatestore.models.requests

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class SearchAddressRequest(
    @SerializedName("text")
    val text: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lng: Double,
    @SerializedName("\$select")
    val select: String = "roads,nearby",
    @SerializedName("\$filter")
    val filter: String = "distance eq 100km"//"city eq یزد" /*todo province, city, county, region, neighborhood, distance, polygon*/
)

data class SearchAddressResponse(
    @SerializedName("value")
    val items: ArrayList<SearchAddressItem>?,
    //On Error
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("code")
    val code: Int?
) {
    val hasError get() = code != null
}

@Parcelize
data class SearchAddressItem(
    @SerializedName("province")
    val province: String?,
    @SerializedName("county")
    private val county: String?,
    @SerializedName("district")
    val distinct: String?,
    @SerializedName("city")
    private val _city: String?,
    @SerializedName("region")
    val region: String?,
    @SerializedName("neighborhood")
    val neighborhood: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("address")
    val _address: String?,
    @SerializedName("geom")
    val geom: Geom? //todo check this:: this is string for reverse geo but here is long
) : Parcelable {
    val address get() = "$_address، $title"
    val city get() = if (_city.isNullOrBlank()) county else _city
}

data class ReverseGeo(
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("postal_address")
    val postal_address: String? = null,
    @SerializedName("address_compact")
    val address_compact: String? = null,
    @SerializedName("primary")
    val primary: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("poi")
    val poi: String? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("province")
    val province: String? = null,
    @SerializedName("county")
    val county: String? = null,
    @SerializedName("district")
    val district: String? = null,
    @SerializedName("rural_district")
    val rural_district: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("village")
    val village: String? = null,
    @SerializedName("region")
    val region: String? = null,
    @SerializedName("neighbourhood")
    val neighbourhood: String? = null,
    @SerializedName("last")
    val last: String? = null,
    @SerializedName("plaque")
    val plaque: String? = null,
    @SerializedName("postal_code")
    val postal_code: String? = null,
    @SerializedName("geom")
    val geom: Geom? = null,
    //On Error
    @SerializedName("code")
    val code: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
) {
    val hasError get() = code != null
}

@Parcelize
data class Geom(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("coordinates")
    val coordinates: List<String>
) : Parcelable