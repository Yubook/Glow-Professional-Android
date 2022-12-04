package com.youbook.glowpros.ui.terms_privacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TermsPrivacyViewModelFactory(private val repository: TermsPrivacyRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TermsPrivacyViewModel(repository) as T
    }
}