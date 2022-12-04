package com.youbook.glowpros.ui.fragment.dashboard

import com.google.gson.annotations.SerializedName

data class HomeOfferResponse(

	@field:SerializedName("result")
	val result: Result? = null,

	@field:SerializedName("success")
	val success: Boolean? = false,

	@field:SerializedName("message")
	val message: String? = ""
)

data class Offer(

	@field:SerializedName("is_active")
	val isActive: Int? = 0,

	@field:SerializedName("offer_apply_on_service")
	val offerApplyOnService: OfferApplyOnService? = null,

	@field:SerializedName("id")
	val id: Int? = 0,

	@field:SerializedName("offer_name")
	val offerName: String? = "",

	@field:SerializedName("offer_percentage")
	val offerPercentage: Int? = 0
)

data class OfferApplyOnService(

	@field:SerializedName("image")
	val image: String? = "",

	@field:SerializedName("is_active")
	val isActive: Int? = 0,

	@field:SerializedName("price")
	val price: Int? = 0,

	@field:SerializedName("name")
	val name: String? = "",

	@field:SerializedName("required_time")
	val requiredTime: Int? = 0,

	@field:SerializedName("id")
	val id: Int? = 0
)

data class Wallet(
	@field:SerializedName("id")
	val id: Int? = 0,

	@field:SerializedName("amount")
	val amount: String? = "",

	@field:SerializedName("offer_amount")
	val offer_amount: String? = "",
)

data class Result(

	@field:SerializedName("offer")
	val offer: Offer? = null,

	@field:SerializedName("availability")
	val availability: Int? = 0,

	@field:SerializedName("customer_support_chat_id")
	val customer_support_chat_id: String? = "",

	@field:SerializedName("wallet")
	val wallet: Wallet? = null,

	@field:SerializedName("is_read")
	val is_read: Boolean? = false
)
