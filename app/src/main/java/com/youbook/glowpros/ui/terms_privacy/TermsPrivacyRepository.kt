package com.youbook.glowpros.ui.terms_privacy

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class TermsPrivacyRepository(private val api: MyApi) : BaseRepository() {

    // 2 = Driver
    suspend fun getTermsPolicy(
    ) = safeApiCall {
        api.getTermsPolicy("2")
    }

}