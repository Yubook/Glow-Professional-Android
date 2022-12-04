package com.youbook.glowpros.ui.review

import com.google.gson.annotations.SerializedName

data class ReviewResponse(

	@field:SerializedName("result")
	val result: List<ResultItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class FromIdUser(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("distance")
	val distance: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("latest_latitude")
	val latestLatitude: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("latest_longitude")
	val latestLongitude: String? = null,

	@field:SerializedName("average_rating")
	val averageRating: Int? = null,

	@field:SerializedName("profile_approved")
	val profileApproved: Int? = null,

	@field:SerializedName("role_id")
	val roleId: Int? = null,

	@field:SerializedName("service")
	val service: List<Any?>? = null,

	@field:SerializedName("van_number")
	val vanNumber: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("total_rating")
	val totalRating: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
)

data class ToIdUser(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("distance")
	val distance: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("latest_latitude")
	val latestLatitude: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("latest_longitude")
	val latestLongitude: String? = null,

	@field:SerializedName("average_rating")
	val averageRating: Double? = null,

	@field:SerializedName("profile_approved")
	val profileApproved: Int? = null,

	@field:SerializedName("role_id")
	val roleId: Int? = null,

	@field:SerializedName("service")
	val service: List<Any?>? = null,

	@field:SerializedName("van_number")
	val vanNumber: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("total_rating")
	val totalRating: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
)

data class Order(
	val any: Any? = null
)

data class ResultItem(

	@field:SerializedName("image")
	val image: List<ImageItem?>? = null,

	@field:SerializedName("from_id")
	val fromId: Int? = null,

	@field:SerializedName("from_id_user")
	val fromIdUser: FromIdUser? = null,

	@field:SerializedName("rating")
	val rating: Int? = null,

	@field:SerializedName("to_id")
	val toId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("to_id_user")
	val toIdUser: ToIdUser? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("created_at")
	val created_at: String? = null

)

data class ImageItem(

	@field:SerializedName("review_id")
	val reviewId: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
