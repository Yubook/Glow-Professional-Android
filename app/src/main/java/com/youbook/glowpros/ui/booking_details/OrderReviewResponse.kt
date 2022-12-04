package com.youbook.glowpros.ui.booking_details

import com.google.gson.annotations.SerializedName

data class OrderReviewResponse(

	@field:SerializedName("result")
	val result: Result? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class User(

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

data class Driver(

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
	val service: List<ServiceItem?>? = null,

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

data class ImageItem(

	@field:SerializedName("review_id")
	val reviewId: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class ServiceItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("required_time")
	val requiredTime: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class Slot(

	@field:SerializedName("timing_id")
	val timingId: Int? = null,

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("driver_id")
	val driverId: Int? = null,

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("is_booked")
	val isBooked: Int? = null
)

data class Service(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("required_time")
	val requiredTime: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null
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
	val averageRating: Int? = null,

	@field:SerializedName("profile_approved")
	val profileApproved: Int? = null,

	@field:SerializedName("role_id")
	val roleId: Int? = null,

	@field:SerializedName("service")
	val service: List<ServiceItem?>? = null,

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

	@field:SerializedName("stripe_key")
	val stripeKey: String? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("user_review")
	val userReview: Boolean? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("discount")
	val discount: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("slot")
	val slot: Slot? = null,

	@field:SerializedName("driver")
	val driver: Driver? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("group_id")
	val groupId: String? = null,

	@field:SerializedName("service")
	val service: Service? = null,

	@field:SerializedName("transaction_number")
	val transactionNumber: String? = null,

	@field:SerializedName("driver_review")
	val driverReview: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("is_order_complete")
	val isOrderComplete: Int? = null
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

data class Result(

	@field:SerializedName("image")
	val image: List<ImageItem?>? = null,

	@field:SerializedName("from_id")
	val fromId: Int? = null,

	@field:SerializedName("from_id_user")
	val fromIdUser: FromIdUser? = null,

	@field:SerializedName("rating")
	val rating: Int? = 0,

	@field:SerializedName("to_id")
	val toId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("to_id_user")
	val toIdUser: ToIdUser? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("order")
	val order: Order? = null
)
