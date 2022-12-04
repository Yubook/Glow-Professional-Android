package com.youbook.glowpros.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class LoginViewModelFactory(private val loginRepository: LoginRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(loginRepository) as T
    }
}