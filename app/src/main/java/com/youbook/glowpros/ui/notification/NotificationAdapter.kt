package com.youbook.glowpros.ui.notification

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.databinding.ItemNotificationBinding
import com.youbook.glowpros.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter (private val context: Context) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private val notificationItem = ArrayList<ResultItem>()

    class ViewHolder (val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemBinding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notificationItem[position]
        val localTime: Calendar =
            Utils.getLocalTime(notification.createdAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")!!
        @SuppressLint("SimpleDateFormat") val time =
            SimpleDateFormat("dd MMMM, yyyy hh:mm a").format(localTime.time)

        holder.binding.tvMessage.text = notification.message
        holder.binding.tvNotificationTime.text = time

    }

    override fun getItemCount(): Int  = notificationItem.size


    fun updateList(newNotification: List<ResultItem>) {
        notificationItem.clear()
        notificationItem.addAll(newNotification)
        notifyDataSetChanged()
    }
}