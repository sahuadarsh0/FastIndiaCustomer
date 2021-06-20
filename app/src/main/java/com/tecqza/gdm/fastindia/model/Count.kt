package com.tecqza.gdm.fastindia.model

import com.google.gson.annotations.SerializedName

data class Count(

	@field:SerializedName("pending")
	val pending: String? = null,

	@field:SerializedName("delivered")
	val delivered: String? = null
)
