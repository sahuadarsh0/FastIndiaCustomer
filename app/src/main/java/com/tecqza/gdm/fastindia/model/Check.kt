package com.tecqza.gdm.fastindia.model

import com.google.gson.annotations.SerializedName

data class Check(
	val purpose: String? = null,

    @field:SerializedName("continue")
	val jsonContinue: String? = null,
	val browser: String? = null,
	val name: String? = null,
	val admin: String? = null,
	val status: String? = null
)

