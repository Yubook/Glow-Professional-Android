package com.youbook.glowpros.ui.booking_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BookingDetailsViewModelFactory(private val repository: BookingDetailsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookingDetailsViewModel(repository) as T
    }
}
