package com.youbook.glowpros.ui.review

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class ReviewRepository constructor(private val api : MyApi) : BaseRepository() {

    suspend fun getReview(
    ) = safeApiCall {
        api.getReview()
    }
}
