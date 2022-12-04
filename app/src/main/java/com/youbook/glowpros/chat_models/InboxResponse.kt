package com.youbook.glowpros.chat_models

import com.google.gson.annotations.SerializedName

data class InboxResponse(

	@field:SerializedName("success")
	val success: Int? = null,

	@field:SerializedName("admin")
	val admin: List<UsersItem?>? = null,

	@field:SerializedName("barbers")
	val barbers: List<Any?>? = null,

	@field:SerializedName("users")
	val users: List<UsersItem?>? = null
)

data class UsersItem(

	@field:SerializedName("unread_count")
	val unreadCount: Int? = null,

	@field:SerializedName("last_active_time")
	val lastActiveTime: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("group_id")
	val groupId: Int? = null,

	@field:SerializedName("profile")
	val profile: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("web_profile_pic")
	val webProfilePic: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("order")
	val order: List<OrderItem?>? = null
)

data class AdminItem(

	@field:SerializedName("unread_count")
	val unreadCount: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("last_active_time")
	val lastActiveTime: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("group_id")
	val groupId: Int? = null,

	@field:SerializedName("profile")
	val profile: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("web_profile_pic")
	val webProfilePic: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
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

data class Category(

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class OrderItem(

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("discount")
	val discount: Int? = null,

	@field:SerializedName("barber_id")
	val barberId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("service")
	val service: List<ServiceItem?>? = null,

	@field:SerializedName("transaction_number")
	val transactionNumber: String? = null,

	@field:SerializedName("review")
	val review: List<Any?>? = null,

	@field:SerializedName("order_id")
	val orderId: Int? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("is_order_complete")
	val isOrderComplete: Int? = null
)

data class CategoryName(

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class ServiceItem(

	@field:SerializedName("service_time")
	val serviceTime: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("slot_date")
	val slotDate: String? = null,

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
