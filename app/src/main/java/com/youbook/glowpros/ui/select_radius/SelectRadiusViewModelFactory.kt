package com.youbook.glowpros.ui.select_radius

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SelectRadiusViewModelFactory(private val repository: SelectRadiusRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SelectRadiusViewModel(repository) as T
    }
}