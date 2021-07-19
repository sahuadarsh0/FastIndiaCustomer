package com.tecqza.gdm.fastindia.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeItem(
    @SerializedName("category_id")
    val categoryId: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("slider")
    val slider: ArrayList<Slider>?,
    @SerializedName("vendor")
    val vendor: ArrayList<Vendor>?
) : Parcelable