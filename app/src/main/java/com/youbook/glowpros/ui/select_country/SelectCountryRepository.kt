package com.youbook.glowpros.ui.select_country

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository


class SelectCountryRepository constructor(private val api : MyApi) : BaseRepository() {

    suspend fun getCountryCode() = safeApiCall {
        api.getCountryCode()
    }

}