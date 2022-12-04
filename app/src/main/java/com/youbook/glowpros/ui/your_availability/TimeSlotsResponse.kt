package com.youbook.glowpros.ui.your_availability

import com.google.gson.annotations.SerializedName

data class TimeSlotsResponse(

	@field:SerializedName("result")
	val result: List<ResultItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ResultItem(

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("time")
	val time: String? = null,

	var isSelected: Boolean = false
)
