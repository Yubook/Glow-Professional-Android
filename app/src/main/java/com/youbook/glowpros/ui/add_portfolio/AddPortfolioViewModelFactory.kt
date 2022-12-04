package com.youbook.glowpros.ui.add_portfolio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class AddPortfolioViewModelFactory(private val repository: AddPortfolioRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddPortfolioViewModel(repository) as T
    }
}