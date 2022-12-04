package com.youbook.glowpros.ui.select_radius

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class SelectRadiusRepository (private val api: MyApi) : BaseRepository(){
    suspend fun addDriverRadius(
        params: Map<String,String>
    ) = safeApiCall {
        api.addDriverRadius(params)
    }

}