package com.eclipsa.fadedriver.ui.select_country

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.youbook.glowpros.ui.select_country.SelectCountryRepository
import com.youbook.glowpros.ui.select_country.SelectCountryViewModel


class SelectCountryViewModelFactory(private val repository: SelectCountryRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SelectCountryViewModel(repository) as T
    }
}