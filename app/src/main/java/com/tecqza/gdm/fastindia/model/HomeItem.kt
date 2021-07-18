package com.tecqza.gdm.fastindia.model


import com.google.gson.annotations.SerializedName

data class HomeItem(
    @SerializedName("category_id")
    val categoryId: String?,
    @SerializedName("edit_info")
    val editInfo: String?,
    @SerializedName("entry_info")
    val entryInfo: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("slider")
    val slider: List<Slider>?,
    @SerializedName("vendor")
    val vendor: List<Vendor>?
)