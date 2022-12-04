package com.youbook.glowpros.ui.fragment.profile_frag
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class ProfileFragRepository constructor(private val api : MyApi): BaseRepository() {

    suspend fun logoutUser(
    ) = safeApiCall {
        api.logout()
    }

    suspend fun getOffer(
        params: Map<String,String>
    ) = safeApiCall {
        api.getOffer(params)
    }

    suspend fun barberOnOff(
        status: Int
    ) = safeApiCall {
        api.barberOnOff(status)
    }
}