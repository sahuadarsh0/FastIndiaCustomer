package com.tecqza.gdm.fastindia.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Products(

    @PrimaryKey(autoGenerate = true)
    val pid: Int? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @ColumnInfo(name = "product_id")
    @field:SerializedName("product_id")
    val productId: String? = null,

    @ColumnInfo(name = "vendor_id")
    @field:SerializedName("vendor_id")
    val vendorId: String? = null,

    @field:SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @ColumnInfo(name = "mrp")
    @field:SerializedName("mrp")
    val mrp: String? = null,

    @ColumnInfo(name = "qty")
    val qty: String? = null,

    @ColumnInfo(name = "selling")
    @field:SerializedName("selling")
    val selling: String? = null

)
