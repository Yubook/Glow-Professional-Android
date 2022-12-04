package com.youbook.glowpros.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Path
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.youbook.glowpros.MainActivity

import com.youbook.glowpros.ui.chat.ChatActivity
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {


    private val TAG = "MyFirebaseMsgService"
    private val CHAT = "chat"
    var NOTIFICATION_COUNT = 1
    private var context: Context? = null
    private val VIDEO_CALL = "VideoCall"
    private val AUDIO_CALL = "AudioCall"

    val IS_NAVIGATE = "IS_NAVIGATE"

    private var service: MyFirebaseMessagingService? = null
    var r: Ringtone? = null

    fun getService(): MyFirebaseMessagingService? {
        return service
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        service = this
        context = applicationContext
        val data: MutableMap<*, *> = remoteMessage.data
        val `object` = JSONObject(remoteMessage.data as Map<*, *>)
        Log.e(TAG, "Map Data : " + remoteMessage.data)
        Log.e(TAG, "Map Notification : " + remoteMessage.notification)
        Log.e(
            TAG,
            "NfType Payload : " + GsonBuilder().setPrettyPrinting().create().toJson(`object`)
        )
        Log.e(TAG, "remoteObject : $remoteMessage")
        Log.e(TAG, "Full Payload : $data")

        sendNotification(
            Objects.requireNonNull(data["title"]).toString(),
            Objects.requireNonNull(data["body"]).toString(), null,
            -1, data, Objects.requireNonNull(data["notification_type"]).toString()
        )
    }

    fun getBitmapFromURL(data: Map<*, *>, type: String, id: Int) {
        try {
            if (Objects.requireNonNull(data["user_profile"]).toString() == "") {
                sendNotification(
                    Objects.requireNonNull(data["title"]).toString(),
                    Objects.requireNonNull(data["body"]).toString(), null, id, data, type
                )
                return
            }
            val url = URL(Objects.requireNonNull(data["user_profile"]).toString())
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            val myBitmap = BitmapFactory.decodeStream(input)
            try {
                sendNotification(
                    Objects.requireNonNull(data["title"]).toString(),
                    Objects.requireNonNull(data["body"]).toString(),
                    getBitmapClippedCircle(myBitmap),
                    id,
                    data,
                    type
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getBitmapClippedCircle(bitmap: Bitmap): Bitmap? {
        val width = bitmap.width
        val height = bitmap.height
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val path = Path()
        path.addCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            Math.min(width, height / 2).toFloat(),
            Path.Direction.CCW
        )
        val canvas = Canvas(outputBitmap)
        canvas.clipPath(path)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        return outputBitmap
    }

    override fun onNewToken(token: String) {
        Log.e(TAG, "Refreshed token: $token")
    }

    private fun sendNotification(
        title: String,
        messageBody: String,
        bitmap: Bitmap?,
        id: Int,
        data: Map<*, *>,
        type: String
    ) {
        val intent: Intent

        if (type == "2") {
            intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(IS_NAVIGATE, "chat")
            intent.putExtra(Constants.INTENT_KEY_CHAT_TITLE, data["title"].toString())
            intent.putExtra(Constants.INTENT_KEY_CHAT_GROUP_ID, data["group_id"].toString())
        } else {
            intent = Intent(this, MainActivity::class.java)
            intent.putExtra(IS_NAVIGATE, "1")
            intent.putExtra(Constants.INTENT_KEY_CHAT_TITLE, "")
            intent.putExtra(Constants.INTENT_KEY_CHAT_GROUP_ID, "")
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId = applicationContext.packageName
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)

        //notificationBuilder.setUsesChronometer(true);
        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
            .setColor(ResourcesCompat.getColor(context!!.resources, R.color.app_black, null))
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSound(defaultSoundUri)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notificationManager = NotificationManagerCompat.from(this)

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, getString(R.string.app_name) + " Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notifications for " + getString(R.string.app_name)
            notificationManager.createNotificationChannel(channel)
        }
        if (type == VIDEO_CALL) {
            notificationManager.notify(id, notificationBuilder.build())
        } else if (type == AUDIO_CALL) {
            notificationManager.notify(id, notificationBuilder.build())
        } else {
            notificationManager.notify(NOTIFICATION_COUNT, notificationBuilder.build())
            NOTIFICATION_COUNT += 1
        }
    }

}