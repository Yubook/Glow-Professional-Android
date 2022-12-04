package com.youbook.glowpros.ui.notification

import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.repository.BaseRepository

class NotificationListRepository(private val api: MyApi) : BaseRepository() {

    suspend fun getNotification(
    ) = safeApiCall {
        api.getNotification()
    }

    suspend fun readNotification(
    ) = safeApiCall {
        api.readNotification()
    }

}