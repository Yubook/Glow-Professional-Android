package com.youbook.glowpros.ui.show_case

import com.google.gson.annotations.SerializedName

data class BarberPortfolioResponse(

	@field:SerializedName("result")
	val result: Result? = Result(),

	@field:SerializedName("success")
	val success: Boolean? = false,

	@field:SerializedName("message")
	val message: String? = ""
)

data class BarberPortfolio(

	@field:SerializedName("data")
	val data: List<DataItem?>? = ArrayList(),

	@field:SerializedName("meta")
	val meta: Meta? = Meta(),

	@field:SerializedName("links")
	val links: Links? = Links()
)

data class Result(

	@field:SerializedName("reviewPortfolio")
	val reviewPortfolio: ReviewPortfolio? = ReviewPortfolio(),

	@field:SerializedName("barberPortfolio")
	val barberPortfolio: BarberPortfolio? = null
)

data class ReviewPortfolio(

	@field:SerializedName("data")
	val data: List<DataItem?>? = ArrayList(),

	@field:SerializedName("meta")
	val meta: Meta? = Meta(),

	@field:SerializedName("links")
	val links: Links? = Links()
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

data class DataItem(

	@field:SerializedName("path")
	val path: String? = "",

	@field:SerializedName("barber_id")
	val barberId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class Meta(

	@field:SerializedName("path")
	val path: String? = "",

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("from")
	val from: Any? = null,

	@field:SerializedName("to")
	val to: Any? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null
)
