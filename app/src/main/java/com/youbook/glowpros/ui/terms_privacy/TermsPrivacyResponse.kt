package com.youbook.glowpros.ui.terms_privacy

import com.google.gson.annotations.SerializedName

data class TermsPrivacyResponse(

	@field:SerializedName("result")
	val result: List<ResultItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ResultItem(

	@field:SerializedName("terms_description")
	val termsDescription: String? = null,

	@field:SerializedName("selection")
	val selection: String? = null,

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("for")
	val jsonMemberFor: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("terms_name")
	val termsName: String? = null
)
