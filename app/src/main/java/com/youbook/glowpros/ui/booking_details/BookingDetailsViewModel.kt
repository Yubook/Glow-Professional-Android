package com.youbook.glowpros.ui.booking_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.youbook.glowpros.base.BaseViewModel
import com.youbook.glowpros.network.Resource
import kotlinx.coroutines.launch

class BookingDetailsViewModel constructor(private val repository: BookingDetailsRepository) : BaseViewModel(repository){

    private val _orderReviewResponse: MutableLiveData<Resource<OrderReviewResponse>> = MutableLiveData()
    val orderReviewResponse: LiveData<Resource<OrderReviewResponse>>
        get() = _orderReviewResponse

    suspend fun getOrderReview(
        order_id: String?,
        from_id: String?,
        to_id: String?
    ) = viewModelScope.launch {
        _orderReviewResponse.value = Resource.Loading
        _orderReviewResponse.value = repository.getOrderReview(order_id,from_id,to_id)
    }
}

