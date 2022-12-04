package com.android.fade.chat_models.chat_messages

import com.google.gson.annotations.SerializedName

data class ChatDataResponse(

	@field:SerializedName("success")
	val success: Int? = null,

	@field:SerializedName("messageData")
	val messageData: List<MessageDataItem?>? = null
)

data class User(

	@field:SerializedName("gender")
	val gender: Any? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("latest_latitude")
	val latest_latitude: Any? = null,

	@field:SerializedName("api_token")
	val api_token: String? = null,

	@field:SerializedName("rating")
	val rating: Int? = null,

	@field:SerializedName("created_at")
	val created_at: String? = null,

	@field:SerializedName("latest_longitude")
	val latest_longitude: Any? = null,

	@field:SerializedName("driver_licence")
	val driver_licence: Any? = null,

	@field:SerializedName("profile_approved")
	val profile_approved: Int? = null,

	@field:SerializedName("van_name")
	val van_name: Any? = null,

	@field:SerializedName("last_active_time")
	val last_active_time: Int? = null,

	@field:SerializedName("updated_at")
	val updated_at: String? = null,

	@field:SerializedName("role_id")
	val role_id: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("licence_image")
	val licence_image: Any? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("van_image")
	val van_image: Any? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("is_active")
	val is_active: Int? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("email_verified_at")
	val email_verified_at: Any? = null,

	@field:SerializedName("services")
	val services: Any? = null,

	@field:SerializedName("van_document")
	val van_document: Any? = null,

	@field:SerializedName("login_status")
	val login_status: String? = null,

	@field:SerializedName("van_number")
	val van_number: Any? = null,

	@field:SerializedName("name")
	val name: String? = ""
)

data class MessageDataItem(

	var header : String = "",

	@field:SerializedName("is_read")
	val is_read: Int? = 0,

	@field:SerializedName("filename")
	val filename: String? = "",

	/*@field:SerializedName("webfile")
	val webfile: Any? = null,*/

	@field:SerializedName("updated_at")
	val updated_at: String? = "",

	@field:SerializedName("chat_time")
	val chat_time: String? = "",

	@field:SerializedName("user_id")
	val user_id: Int? = 0,

	@field:SerializedName("group_id")
	val group_id: Int? = 0,

	@field:SerializedName("created_at")
	val created_at: String? = "",

	@field:SerializedName("id")
	val id: Int? = 0,

	@field:SerializedName("message")
	val message: String? = "",

	@field:SerializedName("type")
	val type: Int? = 0,

	@field:SerializedName("user")
	val user: User? = null
)
