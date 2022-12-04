package com.youbook.glowpros.ui.your_availability

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class YourAvailabilityViewModelFactory(private val yourAvailabilityRepository: YourAvailabilityRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return YourAvailabilityViewModel(yourAvailabilityRepository) as T
    }
}