package com.youbook.glowpros.ui.code_verify

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class CodeVerifyRepository constructor(private val api : MyApi) : BaseRepository() {

    suspend fun login(
        mobile: String,phone_code : String
    ) = safeApiCall {
        api.login(mobile, phone_code)
    }

}
