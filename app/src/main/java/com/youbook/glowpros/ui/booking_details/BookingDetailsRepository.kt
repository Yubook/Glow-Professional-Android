package com.youbook.glowpros.ui.booking_details

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class BookingDetailsRepository constructor(private val api: MyApi) : BaseRepository() {

    suspend fun getOrderReview(
        order_id: String?,
        from_id: String?,
        to_id: String?
    ) = safeApiCall {
        api.getOrderReview(order_id, from_id, to_id)
    }
}
