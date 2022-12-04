package com.youbook.glowpros.ui.barber_terms_policy

import com.google.gson.annotations.SerializedName

data class BarberProfileResponse(

	@field:SerializedName("result")
	val result: Result? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class PolicyAndTerm(

	@field:SerializedName("barber_id")
	val barberId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("content")
	val content: String? = null
)

data class Result(

	@field:SerializedName("fivestar")
	val fivestar: Int? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("profile")
	val profile: String? = null,

	@field:SerializedName("mobile")
	val mobile: String? = null,

	@field:SerializedName("portfolios")
	val portfolios: List<Any?>? = null,

	@field:SerializedName("average_rating")
	val averageRating: Int? = null,

	@field:SerializedName("twostar")
	val twostar: Int? = null,

	@field:SerializedName("services")
	val services: List<Any?>? = null,

	@field:SerializedName("policy_and_term")
	val policyAndTerm: PolicyAndTerm? = null,

	@field:SerializedName("threestar")
	val threestar: Int? = null,

	@field:SerializedName("barber_id")
	val barberId: Int? = null,

	@field:SerializedName("reviews")
	val reviews: List<Any?>? = null,

	@field:SerializedName("onestar")
	val onestar: Int? = null,

	@field:SerializedName("fourstar")
	val fourstar: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("total_reviews")
	val totalReviews: Int? = null
)
