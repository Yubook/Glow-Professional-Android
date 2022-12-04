package com.youbook.glowpros.ui.barber_terms_policy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BarberTermsViewModelFactory(private val repository: BarberTermsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BarberTermsViewModel(repository) as T
    }
}