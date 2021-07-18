package com.tecqza.gdm.fastindia.model


import com.google.gson.annotations.SerializedName

data class HomeItem(
    @SerializedName("category_id")
    val categoryId: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("slider")
    val slider: List<Slider>?,
    @SerializedName("vendor")
    val vendor: List<Vendor>?
)