package com.youbook.glowpros.ui.show_case

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class ShowCaseRepository constructor(private val api: MyApi) : BaseRepository() {

    suspend fun getBarberPortfolio(
    ) = safeApiCall {
        api.getBarberPortfolio()
    }

    suspend fun deletePortfolioImage(
        portfolioId: String
    ) = safeApiCall {
        api.removePortfolio(portfolioId)
    }
}