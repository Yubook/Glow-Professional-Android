package com.youbook.glowpros.ui.fragment.booking_list

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ItemBookingBinding
import com.youbook.glowpros.extension.loadingImage
import com.youbook.glowpros.extension.visible
import com.youbook.glowpros.ui.booking_details.BookingDetailsActivity
import com.youbook.glowpros.ui.chat.ChatActivity
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Utils
import com.youbook.glowpros.utils.Utils.enable
import com.youbook.glowpros.utils.Utils.toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BookingListAdapter(
    private val context: Context,
    private val cancelReasonList: List<ResultItem>,
    private val onItemClick: OnItemClick
) :
    RecyclerView.Adapter<BookingListAdapter.ViewHolder>() {
    private var orderItem = ArrayList<DataItem>()
    private var orderType: String = ""
    private var selectedReason: String = ""
    private var orderId: String = ""
    private var userId: String = ""

    class ViewHolder(val binding: ItemBookingBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = orderItem[position]

        holder.binding.tvUserName.text = item.user!!.name
        var services: String? = ""
        for (i in item.service!!.indices) {
            if (i == 0) {
                services += item.service[i]!!.serviceName.toString()
            } else {
                services += ", "
                services += item.service[i]!!.serviceName.toString()
            }
        }
        holder.binding.tvBookedService.text = services
        holder.binding.tvTotalPrice.text =
            context.getString(R.string.pound_sign).plus(item.amount.toString())
        val slotTime = Utils.formatDate(
            "hh:mm", "hh:mm a",
            item.service[0]!!.slotTime!!.substring(0, 4)
        )

        val orderDate =
            Utils.formatDate("yyyy-MM-dd", "EEEE, dd MMM yyyy", item.service[0]!!.slotDate!!)
                .plus(" @ ").plus(
                    slotTime
                )
        holder.binding.tvBookingDate.text = orderDate

        loadingImage(
            context,
            Constants.STORAGE_URL.plus(item.user.profile.toString()),
            holder.binding.ivUserImage,
            false
        )

        if (orderType == Constants.ORDER_TYPE_PREVIOUS) {
            holder.binding.tvOrderStatus.visibility = View.VISIBLE
        } else {
            holder.binding.tvOrderStatus.visibility = View.VISIBLE
        }

        when (item.isOrderComplete) {
            0 -> {
                holder.binding.tvOrderStatus.text = Constants.ORDER_STATUS_PENDING
            }
            1 -> {
                holder.binding.tvOrderStatus.text = Constants.ORDER_STATUS_COMPLETED
            }
            2 -> {
                holder.binding.tvOrderStatus.text = Constants.ORDER_STATUS_CANCELLED
            }
        }

        holder.itemView.setOnClickListener {
            var reviewImages = ""
            val intent = Intent(context, BookingDetailsActivity::class.java)
            intent.putExtra(Constants.USER_ID, item.user.id.toString())
            intent.putExtra(Constants.ORDER_ID, item.id.toString())
            intent.putExtra(Constants.USER_NAME, item.user.name)
            intent.putExtra(Constants.USER_PROFILE_IMAGE, item.user.profile)
            intent.putExtra(Constants.USER_MOBILE_NO, item.user.mobile.toString())
            intent.putExtra(Constants.EMAIL_ID, item.user.email)
            intent.putExtra(Constants.BOOKED_SERVICE, services)
            intent.putExtra(Constants.ORDER_PRICE, item.amount)
            intent.putExtra(Constants.BOOKED_DATE, orderDate)
            intent.putExtra(Constants.ORDER_LAT, item.latitude.toString())
            intent.putExtra(Constants.ORDER_LON, item.longitude.toString())

            intent.putExtra(Constants.IS_CHAT_ENABLE, item.chat)
            intent.putExtra(Constants.GROUP_ID, item.chat_group_id)

            if (item.review!!.isNotEmpty()) {
                intent.putExtra(Constants.SERVICE_RATING, item.review[0]?.service.toString())
                intent.putExtra(Constants.HYGIENE_RATING, item.review[0]?.hygiene.toString())
                intent.putExtra(Constants.VALUE_RATING, item.review[0]?.value.toString())
                item.review[0]!!.reviewImages!!.forEach {
                    reviewImages += it!!.image.toString() + ","
                }
                intent.putExtra(Constants.REVIEW_IMAGES, reviewImages)
            } else {
                intent.putExtra(Constants.SERVICE_RATING, "")
                intent.putExtra(Constants.HYGIENE_RATING, "")
                intent.putExtra(Constants.VALUE_RATING, "")
                intent.putExtra(Constants.REVIEW_IMAGES, "")
            }

            intent.putExtra(Constants.BARBER_DISTANCE, item.distance)

            context.startActivity(intent)
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val currentTime: String = sdf.format(Date())
        val currentTimeDate = sdf.parse(currentTime)

        if (item.service.isNotEmpty()) {
            if (item.service[0]!!.slotTime.toString().length == 4) {
                item.service[0]!!.slotTime = "0" + item.service[0]!!.slotTime
            }

            val bookedSlotEndTimeHour = ((item.service[0]!!.slotTime.toString().substring(
                item.service[0]!!.slotTime.toString().length - 5
            ).substring(0, 2)).toInt() + 1).toString()

            val bookedSlotEndTimeMinute = (item.service[0]!!.slotTime.toString().substring(
                item.service[0]!!.slotTime.toString().length - 5
            ).substring(2, 5))

            val bookedSlotNewEndTime = bookedSlotEndTimeHour.plus(bookedSlotEndTimeMinute)
            val bookedSlotStartTime =
                item.service[0]!!.slotDate.plus(" ")
                    .plus(item.service[0]!!.slotTime!!.substring(0, 5))
            val bookedSlotEndTime = item.service[0]!!.slotDate.plus(" ").plus(bookedSlotNewEndTime)
            val bookedSlotStartTimeDate = sdf.parse(bookedSlotStartTime)
            val bookedSlotEndTimeDate = sdf.parse(bookedSlotEndTime)

            val orderSlotStartTime =
                (item.service[0]!!.slotTime.toString().substring(0, 2)).toInt() - 1
            val orderSlotEndTime = (item.service[0]!!.slotTime.toString().substring(2, 5))
            val orderSlotStartTime1 = item.service[0]!!.slotDate.plus(" ")
                .plus(orderSlotStartTime.toString().plus(orderSlotEndTime))
            var orderSlotEndChatTime = item.service[0]!!.slotDate.plus(" ").plus(
                item.service[0]!!.slotTime.toString()
                    .substring(item.service[0]!!.slotTime.toString().length - 5)
            )

            val slotDate = sdf.parse(orderSlotStartTime1)
            val slotEndChatDate = sdf.parse(orderSlotEndChatTime)

            if (orderType == Constants.ORDER_TYPE_FUTURE) {
                holder.binding.tvOrderCancel.visible(true)
                holder.binding.tvOrderComplete.visible(true)
                holder.binding.ivChat.visible(true)
                disableOrderCompleteButton(holder)
                disableChatButton(holder)
            } else if (orderType == Constants.ORDER_TYPE_CURRENT) {
                holder.binding.tvOrderComplete.visible(true)
                holder.binding.tvOrderCancel.visible(true)
                holder.binding.ivChat.visible(true)
                if (currentTimeDate.after(bookedSlotStartTimeDate) && currentTimeDate.before(
                        bookedSlotEndTimeDate
                    )
                ) {
                    holder.binding.tvOrderComplete.enable(true)
                } else {
                    holder.binding.tvOrderComplete.enable(false)
                }

                if (currentTimeDate.after(bookedSlotStartTimeDate)) {
                    holder.binding.tvOrderCancel.enable(false)
                } else {
                    holder.binding.tvOrderCancel.enable(true)
                }

                // This logic implemented before
                /*if (currentTimeDate.after(slotDate) && currentTimeDate.before(slotEndChatDate)) {
                    holder.binding.ivChat.enable(true)
                } else {
                    holder.binding.ivChat.enable(false)
                }*/

                if (item.chat!!) {
                    holder.binding.ivChat.enable(true)
                } else {
                    holder.binding.ivChat.enable(false)
                }


            } else {
                // previous or else
                holder.binding.tvOrderCancel.visible(false)

                holder.binding.tvOrderComplete.visible(false)

                holder.binding.ivChat.visible(false)
            }

            holder.binding.tvOrderCancel.setOnClickListener {
                if (Utils.isConnected(context)) {
                    openCancelOrderDialog(item.id, item.user.id)
                } else {
                    context.toast(context.getString(R.string.no_internet_connection))
                }

            }

            holder.binding.tvOrderComplete.setOnClickListener {
                if (Utils.isConnected(context)) {
                    onItemClick.onCompleteOrder(item.id.toString())
                } else {
                    context.toast(context.getString(R.string.no_internet_connection))
                }
            }

            holder.binding.ivChat.setOnClickListener {
                if (Utils.isConnected(context)) {
                    val intent = Intent(context, ChatActivity::class.java)
                    intent.putExtra(Constants.INTENT_KEY_CHAT_TITLE, item.user.name)
                    intent.putExtra(Constants.INTENT_KEY_CHAT_GROUP_ID, item.chat_group_id)
                    context.startActivity(intent)

                } else {
                    context.toast(context.getString(R.string.no_internet_connection))
                }
            }
        }
    }

    private fun disableOrderCompleteButton(holder: ViewHolder) {
        holder.binding.tvOrderComplete.enable(false)
    }

    private fun disableChatButton(holder: ViewHolder) {
        holder.binding.ivChat.enable(false)
    }

    override fun getItemCount(): Int = orderItem.size

    fun getList(): List<DataItem> {
        return orderItem
    }

    fun updateList(newOrders: List<DataItem>, type: String) {
        orderItem.clear()
        orderItem.addAll(newOrders)
        orderType = type
        notifyDataSetChanged()
    }

    private fun openCancelOrderDialog(orderId: Int?, userId: Int?) {
        val dialog = Dialog(context, R.style.CustomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.custom_cancel_reasons_dialog)
        val recyclerView = dialog.findViewById(R.id.cancelReasonRecyclerView) as RecyclerView
        val tvSubmit = dialog.findViewById(R.id.tvSubmit) as TextView
        val adapter = CancelReasonsListAdapter(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        adapter.updateList(cancelReasonList)

        tvSubmit.setOnClickListener {

            val reasonsItem = adapter.getSelectedItem(true)
            if (reasonsItem != null && reasonsItem.isNotEmpty()) {

                if (reasonsItem[0].reason.equals("Other")) {
                    if (adapter.otherEditTextReason == "") {
                        Toast.makeText(
                            context,
                            context.getString(R.string.please_add_reason),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        dialog.dismiss()
                        onItemClick.onClick(
                            adapter.otherEditTextReason,
                            orderId.toString(),
                            userId.toString()
                        )
                    }
                } else {
                    dialog.dismiss()
                    onItemClick.onClick(
                        reasonsItem[0].reason,
                        orderId.toString(),
                        userId.toString()
                    )
                }

            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.please_select_any_reason),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialog.show()
    }
}