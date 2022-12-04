package com.youbook.glowpros.ui.terms_privacy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.youbook.glowpros.base.BaseViewModel
import com.youbook.glowpros.network.Resource
import kotlinx.coroutines.launch

class TermsPrivacyViewModel constructor(private val repository: TermsPrivacyRepository) :
    BaseViewModel(repository) {

    private val _termsPrivacyResponse: MutableLiveData<Resource<TermsPrivacyResponse>> = MutableLiveData()
    val termsPrivacyResponse: LiveData<Resource<TermsPrivacyResponse>>
        get() = _termsPrivacyResponse

    suspend fun getTermsPolicy(
    ) = viewModelScope.launch {
        _termsPrivacyResponse.value = Resource.Loading
        _termsPrivacyResponse.value = repository.getTermsPolicy()
    }

}