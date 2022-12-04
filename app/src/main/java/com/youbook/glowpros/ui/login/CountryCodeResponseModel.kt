package com.youbook.glowpros.ui.login

import com.google.gson.annotations.SerializedName

data class CountryCodeResponseModel(

	@field:SerializedName("result")
	val result: List<ResultItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ResultItem(

	@field:SerializedName("emojiU")
	val emojiU: String? = null,

	@field:SerializedName("emoji")
	val emoji: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("active")
	val active: Int? = null,

	@field:SerializedName("currency")
	val currency: String? = null,

	@field:SerializedName("phonecode")
	val phonecode: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("iso2")
	val iso2: String? = null,

	@field:SerializedName("region")
	val region: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
)
