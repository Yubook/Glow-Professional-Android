package com.youbook.glowpros.ui.login

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository


class LoginRepository constructor(private val api : MyApi) : BaseRepository() {

    suspend fun getCountryCode(
    ) = safeApiCall {
        api.getCountryCode()
    }

}