package com.tecqza.gdm.fastindia.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val data: Data? = null,
    val otp: Int? = null,
    val error: String? = null,
    val message: String? = null
)

data class Data(

    @field:SerializedName("email_id")
    val emailId: String? = null,
    val image: String? = null,
    val address: String? = null,
    val mobile: String? = null,
    val name: String? = null,

    @field:SerializedName("state_id")
    val stateId: String? = null,

    val photoUrl: String? = null,
    @field:SerializedName("customer_id")
    val customerId: String? = null,

    @field:SerializedName("city_id")
    val cityId: String? = null,

    )

