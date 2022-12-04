package com.youbook.glowpros.ui.profile

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository
import okhttp3.MultipartBody

class ProfileRepository constructor(private val api : MyApi) : BaseRepository() {

    suspend fun addProfile(
        files: List<MultipartBody.Part>,
        params: Map<String,String>
    ) = safeApiCall {
        api.addProfile(files,params)
    }

    suspend fun updateProfileWithPhoto(
        files: List<MultipartBody.Part>,
        params: Map<String,String>
    ) = safeApiCall {
        api.updateProfile(files,params)
    }

    suspend fun updateProfileWithoutPhoto(
        params: Map<String,String>
    ) = safeApiCall {
        api.updateProfileWithoutPhoto(params)
    }

    suspend fun getStates() = safeApiCall {
        api.getStates()
    }

    suspend fun getCities(countryCode: String?) = safeApiCall {
        api.getCities(countryCode)
    }

}
