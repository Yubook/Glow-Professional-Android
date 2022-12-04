package com.youbook.glowpros.ui.show_case

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ShowCaseViewModelFactory(private val repository: ShowCaseRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ShowCaseViewModel(repository) as T
    }
}