package com.youbook.glowpros.ui.select_services

import com.google.gson.annotations.SerializedName

data class ServiceResponseModel(

	@field:SerializedName("result")
	val result: List<ResultItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ResultItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("service_added")
	var serviceAdded: Boolean? = false,

	@field:SerializedName("price")
	val price: String? = "",

	@field:SerializedName("service_id")
	val service_id: String? = "",

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("category")
	val category: Category? = null,

	@field:SerializedName("subcategory")
	val subcategory: Subcategory? = null,

	var isServiceSelected : Boolean = false,

	var servicesPrice: Double? = 0.0
)

data class Category(
	val is_active: Int? = null,
	val name: String? = null,
	val id: Int? = null
)

data class Subcategory(
	val category_name: CategoryName? = null,
	val is_active: Int? = null,
	val name: String? = null,
	val id: Int? = null
)

data class CategoryName(
	val is_active: Int? = null,
	val name: String? = null,
	val id: Int? = null
)
