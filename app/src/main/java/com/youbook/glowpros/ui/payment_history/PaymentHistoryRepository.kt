package com.youbook.glowpros.ui.payment_history


import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class PaymentHistoryRepository constructor(private val api : MyApi) : BaseRepository() {

    suspend fun getUserOrders(
        user_id: String
    ) = safeApiCall {
        api.getPaymentHistory()
    }

}
