package com.tecqza.gdm.fastindia.model

import com.google.gson.annotations.SerializedName

data class StatesCities(

    @field:SerializedName("code")
    val code: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("city_id")
    val cityId: String? = null,

    @field:SerializedName("state_id")
    val stateId: String? = null
)
