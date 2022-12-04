package com.youbook.glowpros.ui.insight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InsightViewModelFactory(private val paymentDetailsRepository: InsightRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InsightViewModel(paymentDetailsRepository) as T
    }
}