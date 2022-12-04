package com.youbook.glowpros.ui.select_services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.youbook.glowpros.base.BaseViewModel
import com.youbook.glowpros.network.CommonResponse
import com.youbook.glowpros.network.Resource
import kotlinx.coroutines.launch

class SelectServiceViewModel constructor(private val repository: SelectServiceRepository) :
    BaseViewModel(repository) {

    private val _getServiceResponse: MutableLiveData<Resource<ServiceResponseModel>> = MutableLiveData()
    val getServiceResponse: LiveData<Resource<ServiceResponseModel>>
        get() = _getServiceResponse

    private val _addBarberServiceResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val addBarberServiceResponse: LiveData<Resource<CommonResponse>>
        get() = _addBarberServiceResponse

    private val _removeServiceResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val removeServiceResponse: LiveData<Resource<CommonResponse>>
        get() = _removeServiceResponse

    private val _barberServiceResponse: MutableLiveData<Resource<ServiceResponseModel>> = MutableLiveData()
    val barberServiceResponse: LiveData<Resource<ServiceResponseModel>>
        get() = _barberServiceResponse

    suspend fun getServices() = viewModelScope.launch {
        _getServiceResponse.value = Resource.Loading
        _getServiceResponse.value = repository.getServices()
    }

    suspend fun getBarberService() = viewModelScope.launch {
        _barberServiceResponse.value = Resource.Loading
        _barberServiceResponse.value = repository.getBarberService()
    }

    suspend fun addBarberService(
        serviceList: List<Int>,
        priceList: List<Double>
    ) = viewModelScope.launch {
        _addBarberServiceResponse.value = Resource.Loading
        _addBarberServiceResponse.value = repository.addBarberService(serviceList, priceList)
    }

    suspend fun removeBarberService(
        serviceId: String,
    ) = viewModelScope.launch {
        _removeServiceResponse.value = Resource.Loading
        _removeServiceResponse.value = repository.removeBarberService(serviceId)
    }


}