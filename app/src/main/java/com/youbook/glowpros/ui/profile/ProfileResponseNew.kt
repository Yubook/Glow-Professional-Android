package com.youbook.glowpros.ui.profile

import com.google.gson.annotations.SerializedName

data class ProfileResponseNew(

	@field:SerializedName("result")
	val result: ResultNew? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class City(

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class DocumentItem(

	@field:SerializedName("document_name")
	val documentName: String? = null,

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class State(

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class ResultNew(

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("city")
	val city: City? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("latest_latitude")
	val latestLatitude: String? = null,

	@field:SerializedName("document1_name")
	val document1_name: String? = null,

	@field:SerializedName("document1_path")
	val document1_path: String? = null,

	@field:SerializedName("document2_name")
	val document2_name: String? = null,

	@field:SerializedName("document2_path")
	val document2_path: String? = null,


	@field:SerializedName("min_radius")
	val minRadius: String? = null,

	@field:SerializedName("latest_longitude")
	val latestLongitude: String? = null,

	@field:SerializedName("profile_approved")
	val profileApproved: Int? = null,

	@field:SerializedName("is_barber_available")
	val isBarberAvailable: Int? = null,

	@field:SerializedName("is_service_added")
	val isServiceAdded: Int? = null,

	@field:SerializedName("role_id")
	val roleId: Int? = null,

	@field:SerializedName("address_line_1")
	val addressLine1: String? = null,

	@field:SerializedName("postal_code")
	val postalCode: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("address_line_2")
	val addressLine2: String? = null,

	/*@field:SerializedName("state")
	val state: State? = null,*/

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("is_availability")
	val isAvailability: Int? = null,

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("profile")
	val profile: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("average_rating")
	val averageRating: Int? = null,

	@field:SerializedName("token")
	val token: String? = null,

	@field:SerializedName("max_radius")
	val maxRadius: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("total_reviews")
	val totalReviews: Int? = null
)
