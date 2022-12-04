package com.youbook.glowpros.ui.add_portfolio

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository
import okhttp3.MultipartBody

class AddPortfolioRepository constructor(private val api: MyApi) : BaseRepository() {

    suspend fun addPortfolioImage(
        imageList: List<MultipartBody.Part>,
    ) = safeApiCall {
        api.addPortfolioImage(imageList)
    }
}