package com.tecqza.gdm.fastindia.model

import com.google.gson.annotations.SerializedName

data class Vendors(

    @field:SerializedName("email_id")
    val emailId: String? = null,

    @field:SerializedName("address")
    val address: String? = null,

    @field:SerializedName("contact_person")
    val contactPerson: String? = null,

    @field:SerializedName("mobile")
    val mobile: String? = null,

    @field:SerializedName("gst")
    val gst: String? = null,

    @field:SerializedName("url")
    val url: String? = null,

    @field:SerializedName("category_id")
    val categoryId: String? = null,

    @field:SerializedName("vendor_id")
    val vendorId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("logo")
    val logo: String? = null,

    @field:SerializedName("state_id")
    val stateId: String? = null,

    @field:SerializedName("pan")
    val pan: String? = null,

    @field:SerializedName("city_id")
    val cityId: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)
