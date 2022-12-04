package com.youbook.glowpros.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youbook.glowpros.network.Resource
import kotlinx.coroutines.launch

class LoginViewModel constructor(private val repository: LoginRepository): ViewModel() {

    private val _countryCodeResponse: MutableLiveData<Resource<CountryCodeResponseModel>> = MutableLiveData()
    val countryCodeResponse: LiveData<Resource<CountryCodeResponseModel>>
        get() = _countryCodeResponse

    suspend fun countryCode(
    ) = viewModelScope.launch {
        _countryCodeResponse.value = Resource.Loading
        _countryCodeResponse.value = repository.getCountryCode()
    }


}