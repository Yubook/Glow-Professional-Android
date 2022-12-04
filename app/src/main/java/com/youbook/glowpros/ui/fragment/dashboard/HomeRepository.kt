package com.youbook.glowpros.ui.fragment.dashboard
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class HomeRepository constructor(private val api : MyApi): BaseRepository() {

    suspend fun getOffer(
        params: Map<String,String>
    ) = safeApiCall {
        api.getOffer(params)
    }

    /*

    suspend fun getNearestDrivers(
    ) = safeApiCall {
        api.getNearestDriver()
    }

    suspend fun searchDriver(
        params: Map<String,String>
    ) = safeApiCall {
        api.searchDriver(params)
    }*/
}