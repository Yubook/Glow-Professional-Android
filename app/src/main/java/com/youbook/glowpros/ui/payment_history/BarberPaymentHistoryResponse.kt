package com.youbook.glowpros.ui.payment_history

import com.google.gson.annotations.SerializedName

data class BarberPaymentHistoryResponse(

	@field:SerializedName("result")
	val result: Result? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Subcategory(

	@field:SerializedName("category_name")
	val categoryName: CategoryName? = null,

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class User(

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("city")
	val city: List<Any?>? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("latest_latitude")
	val latestLatitude: String? = null,

	@field:SerializedName("profile")
	val profile: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("min_radius")
	val minRadius: String? = null,

	@field:SerializedName("latest_longitude")
	val latestLongitude: String? = null,

	@field:SerializedName("average_rating")
	val averageRating: Int? = null,

	@field:SerializedName("profile_approved")
	val profileApproved: Int? = null,

	@field:SerializedName("is_service_added")
	val isServiceAdded: Int? = null,

	@field:SerializedName("max_radius")
	val maxRadius: String? = null,

	@field:SerializedName("role_id")
	val roleId: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("address_line_1")
	val addressLine1: String? = null,

	@field:SerializedName("total_reviews")
	val totalReviews: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("address_line_2")
	val addressLine2: String? = null,

	@field:SerializedName("state")
	val state: List<Any?>? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("is_availability")
	val isAvailability: Int? = null
)

data class CategoryName(

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class Result(

	@field:SerializedName("totalExpense")
	val totalExpense: Int? = null,

	@field:SerializedName("order")
	val order: Order? = null
)

data class Order(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("meta")
	val meta: Meta? = null,

	@field:SerializedName("links")
	val links: Links? = null
)

data class Meta(

	@field:SerializedName("path")
	val path: String? = null,

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("from")
	val from: Int? = null,

	@field:SerializedName("to")
	val to: Int? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null
)

data class DataItem(

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("discount")
	val discount: Int? = null,

	@field:SerializedName("review_images")
	val reviewImages: List<Any?>? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("service")
	val service: List<ServiceItem?>? = null,

	@field:SerializedName("transaction_number")
	val transactionNumber: String? = null,

	@field:SerializedName("review")
	val review: List<Any?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("barber")
	val barber: List<Any?>? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("is_order_complete")
	val isOrderComplete: Int? = null
)

data class Category(

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class Links(

	@field:SerializedName("next")
	val next: Any? = null,

	@field:SerializedName("last")
	val last: String? = null,

	@field:SerializedName("prev")
	val prev: Any? = null,

	@field:SerializedName("first")
	val first: String? = null
)

data class ReviewItem(

	@field:SerializedName("barber_id")
	val barberId: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("service")
	val service: Int? = null,

	@field:SerializedName("hygiene")
	val hygiene: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("order_id")
	val orderId: String? = null,

	@field:SerializedName("value")
	val value: Int? = null
)

data class ServiceItem(

	@field:SerializedName("service_time")
	val serviceTime: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("slot_date")
	var slotDate: String? = null,

	@field:SerializedName("service_price")
	val servicePrice: String? = null,

	@field:SerializedName("service_name")
	val serviceName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("category")
	val category: Category? = null,

	@field:SerializedName("subcategory")
	val subcategory: Subcategory? = null,

	@field:SerializedName("slot_time")
	var slotTime: String? = null
)
