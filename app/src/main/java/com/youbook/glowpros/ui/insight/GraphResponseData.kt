package com.youbook.glowpros.ui.insight

import com.google.gson.annotations.SerializedName

data class GraphResponseData(

    @field:SerializedName("result")
    val result: Result? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)

data class YearlyItem(

    @field:SerializedName("revenue")
    val revenue: String? = null,

    @field:SerializedName("month")
    val month: String? = null,

    @field:SerializedName("year")
    val year: Int? = null,

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("start_week")
    val start_week: String? = null,

    @field:SerializedName("end_week")
    val end_week: String? = null,
)

data class Result(

    @field:SerializedName("yearly")
    val yearly: List<YearlyItem?>? = ArrayList(),

    @field:SerializedName("1Week")
    val weekly: List<YearlyItem?>? = ArrayList(),

    @field:SerializedName("1month")
    val monthly: List<YearlyItem?>? = ArrayList(),

    @field:SerializedName("somemonths")
    val somemonths: List<YearlyItem?>? = ArrayList(),
)
