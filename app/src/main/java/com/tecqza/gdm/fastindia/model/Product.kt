package com.tecqza.gdm.fastindia.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Product(

    @PrimaryKey(autoGenerate = true)
    val pid: Int? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("product_id")
    val productId: String? = null,

    @field:SerializedName("vendor_id")
    val vendorId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("mrp")
    val mrp: String? = null,

    @field:SerializedName("qty")
    val qty: String? = null,

    @field:SerializedName("selling")
    val selling: String? = null

) : Parcelable
