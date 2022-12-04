package com.youbook.glowpros.ui.code_verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CodeVerifyViewModelFactory(private val profileRepository: CodeVerifyRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CodeVerifyViewModel(profileRepository) as T
    }
}
