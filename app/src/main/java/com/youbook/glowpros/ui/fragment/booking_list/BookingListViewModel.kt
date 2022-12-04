package com.youbook.glowpros.ui.fragment.booking_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.youbook.glowpros.base.BaseViewModel
import com.youbook.glowpros.network.CommonResponse
import com.youbook.glowpros.network.Resource
import kotlinx.coroutines.launch

class BookingListViewModel (val repository: BookingListRepository) : BaseViewModel(repository)  {

    private val _userOrderResponse: MutableLiveData<Resource<BarberBookingResponse>> = MutableLiveData()
    val userOrderResponse: LiveData<Resource<BarberBookingResponse>>
        get() = _userOrderResponse

    private val _cancelOrderResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val cancelOrderResponse: LiveData<Resource<CommonResponse>>
        get() = _cancelOrderResponse

    private val _completeOrderResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val completeOrderResponse: LiveData<Resource<CommonResponse>>
        get() = _completeOrderResponse

    private val _cancelReasonsResponse: MutableLiveData<Resource<CancelReasonListResponse>> = MutableLiveData()
    val cancelReasonsResponse: LiveData<Resource<CancelReasonListResponse>>
        get() = _cancelReasonsResponse

    suspend fun getDriverOrders(
    ) = viewModelScope.launch {
        _userOrderResponse.value = Resource.Loading
        _userOrderResponse.value = repository.getDriverOrders()
    }

    suspend fun cancelOrder(
        params: Map<String,String>
    ) = viewModelScope.launch {
        _cancelOrderResponse.value = Resource.Loading
        _cancelOrderResponse.value = repository.cancelOrder(params)
    }

    suspend fun completeOrder(
        params: Map<String,String>
    ) = viewModelScope.launch {
        _completeOrderResponse.value = Resource.Loading
        _completeOrderResponse.value = repository.completeOrder(params)
    }

    suspend fun getCancelReasons(
    ) = viewModelScope.launch {
        _cancelReasonsResponse.value = Resource.Loading
        _cancelReasonsResponse.value = repository.getCancelReasons()
    }
}