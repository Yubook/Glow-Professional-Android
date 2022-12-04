package com.youbook.glowpros.ui.select_services

import com.google.gson.annotations.SerializedName

data class BarberServiceResponse(

	@field:SerializedName("result")
	val result: List<ResultItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Service(

	@field:SerializedName("subcategory_id")
	val subcategoryId: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("category_id")
	val categoryId: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("time")
	val time: Int? = null
)

data class ResultItem1(

	@field:SerializedName("is_active")
	val isActive: Int? = null,

	@field:SerializedName("barber_id")
	val barberId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("service")
	val service: Service? = null,

	@field:SerializedName("service_id")
	val serviceId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null
)
