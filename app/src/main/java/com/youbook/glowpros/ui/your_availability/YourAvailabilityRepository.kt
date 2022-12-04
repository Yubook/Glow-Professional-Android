package com.youbook.glowpros.ui.your_availability

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class YourAvailabilityRepository(private val api: MyApi) : BaseRepository() {

    suspend fun getTimesSlots(
    ) = safeApiCall {
        api.getTimesSlots()
    }

    suspend fun addBarberTime(
        dateList : ArrayList<String>,
        slotTimeList : ArrayList<String>,
    ) = safeApiCall {
        api.addBarberTime(dateList, slotTimeList)
    }

}