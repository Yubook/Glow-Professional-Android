package com.android.fade.ui.chat

import com.youbook.glowpros.ui.chat.User
import com.google.gson.annotations.SerializedName

class MessageDataItem {
    
    var header : String = ""

    val audioPlaying = false

    val STATE_IDLE = 0
    val STATE_INIT = 1
    val STATE_PLAYING = 2

    val audioState = STATE_IDLE

    @SerializedName("is_read")
    val isRead = 0

    @SerializedName("filename")
    val filename: String? = null

    @SerializedName("updated_at")
    val updatedAt: String? = null

    @SerializedName("chat_time")
    val chatTime: String? = null

    @SerializedName("user_id")
    val userId = 0

    @SerializedName("group_id")
    val groupId = 0

    @SerializedName("created_at")
    val createdAt: String? = null

    @SerializedName("id")
    val id = 0

    @SerializedName("message")
    val message: String = ""

    @SerializedName("totalpage")
    var totalPage: String? = ""

    @SerializedName("status")
    var status: String? = ""

    @SerializedName("type")
    val type = 0

    @SerializedName("user")
    val user: User? = null
}