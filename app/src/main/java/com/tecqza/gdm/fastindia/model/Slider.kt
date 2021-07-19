package com.tecqza.gdm.fastindia.model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Slider(
    @SerializedName("category_id")
    val categoryId: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("slider_id")
    val sliderId: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("vendor_id")
    val vendorId: String?
) : Parcelable