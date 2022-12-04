package com.youbook.glowpros.ui.barber_terms_policy

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class BarberTermsRepository(private val api: MyApi) : BaseRepository() {

    suspend fun getDriverProfile(
        barberId: String,
    ) = safeApiCall {
        api.getDriverProfile(barberId)
    }

    suspend fun updateTermsPolicy(
        params: Map<String, String>,
    ) = safeApiCall {
        api.updateTermsPolicy(params)
    }

}