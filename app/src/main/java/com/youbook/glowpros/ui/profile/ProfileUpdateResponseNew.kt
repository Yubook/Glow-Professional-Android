package com.youbook.glowpros.ui.profile

import com.google.gson.annotations.SerializedName

data class ProfileUpdateResponseNew(

	@field:SerializedName("result")
	val result: Result? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

/*data class State(

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)*/

data class Result(

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("city")
	val city: City? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("profile")
	val profile: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("document2_path")
	val document2Path: String? = null,

	@field:SerializedName("role_id")
	val roleId: Int? = null,

	@field:SerializedName("document2_name")
	val document2Name: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("address_line_1")
	val addressLine1: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("address_line_2")
	val addressLine2: String? = null,

	@field:SerializedName("document1_name")
	val document1Name: String? = null,

	@field:SerializedName("state")
	val state: State? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("document1_path")
	val document1Path: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
)

/*data class City(

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)*/
