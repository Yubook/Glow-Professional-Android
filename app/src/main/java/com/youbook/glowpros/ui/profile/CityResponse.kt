package com.youbook.glowpros.ui.profile

import com.google.gson.annotations.SerializedName

data class CityResponse(

	@field:SerializedName("result")
	val result: ArrayList<ResultItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ResultItem(

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("state_id")
	val state_id: Int? = null,

	@field:SerializedName("country_id")
	val country_id: Int? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null


)
