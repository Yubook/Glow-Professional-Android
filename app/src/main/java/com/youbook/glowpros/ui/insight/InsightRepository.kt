package com.youbook.glowpros.ui.insight

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class InsightRepository(private val api: MyApi) : BaseRepository() {

    suspend fun getRevenueData(
        driverId : String,
        searchString: String
    ) = safeApiCall {
        api.getRevenueData(driverId, searchString)
    }

    suspend fun getRevenueDataWithoutSearch(
        driverId : String
    ) = safeApiCall {
        api.getRevenueDataWithoutSearch(driverId)
    }

    suspend fun getMostBookedServices(
    ) = safeApiCall {
        api.getMostBookedServices()
    }
}