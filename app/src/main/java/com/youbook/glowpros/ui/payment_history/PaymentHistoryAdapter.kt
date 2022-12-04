package com.youbook.glowpros.ui.payment_history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ItemPaymentHistoryBinding
import com.youbook.glowpros.extension.loadingImage
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PaymentHistoryAdapter(private val context: Context) :
    RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>() {
    private val orderItem = ArrayList<DataItem>()

    class ViewHolder(val binding: ItemPaymentHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemPaymentHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderItem[position]
        var orderDate: String? = ""
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val currentTime: String = sdf.format(Date())

        if (order.service!!.isNotEmpty()) {

            if(order.service[0]!!.slotTime!!.length == 4){
                order.service[0]!!.slotTime = "0"+ order.service[0]!!.slotTime
            }
            holder.binding.tvServiceName.text = order.service[0]!!.serviceName

            val slotNewDate = order.service.get(0)!!.slotDate.plus(" ").plus(
                order.service[0]!!.slotTime.toString().substring(
                    0,
                    5
                )
            )

            var slotTime = Utils.formatDate(
                "hh:mm", "hh:mm a",
                order.service[0]!!.slotTime!!.substring(0, 5)
            )
            orderDate =
                Utils.formatDate("yyyy-MM-dd", "dd MMM, yyyy", order.service[0]!!.slotDate)
                    .plus(" ").plus(
                    slotTime
                )
        }

        /*if (order.isOrderComplete!!.toString() == "1") {
            holder.binding.tvServicePrice.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.green
                )
            )
        } else if (order.isOrderComplete.toString() == "2") {
            holder.binding.tvServicePrice.setTextColor(ContextCompat.getColor(context, R.color.red))
        }*/

        when(order.isOrderComplete){
            0 ->{
                // Pending
                holder.binding.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                holder.binding.tvOrderStatus.setBackgroundResource(R.drawable.drawable_pending_order_text_border)
                holder.binding.tvOrderStatus.text = Constants.OrderPending
            }
            1 ->{
                // Completed
                holder.binding.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.green))
                holder.binding.tvOrderStatus.setBackgroundResource(R.drawable.drawable_completed_order_text_border)
                holder.binding.tvOrderStatus.text = Constants.OrderCompleted
            }
            2 ->{
                // Rejected or Cancelled
                holder.binding.tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.red))
                holder.binding.tvOrderStatus.setBackgroundResource(R.drawable.drawable_cancelled_order_text_border)
                holder.binding.tvOrderStatus.text = Constants.OrderCancelled
            }

        }


        holder.binding.tvUserName.text = order.user!!.name
        loadingImage(
            context,
            Constants.STORAGE_URL.plus(order.user.profile),
            holder.binding.ivDriverImage,
            true
        )

        holder.binding.tvServiceDate.text = orderDate
        holder.binding.tvServicePrice.text =
            context.getText(R.string.pound_sign).toString().plus(" ")
                .plus(order.amount!!.toDouble().toString())

    }


    override fun getItemCount(): Int = orderItem.size

    fun getList(): List<DataItem> {
        return orderItem
    }

    fun updateList(newOrders: List<DataItem>) {
        orderItem.clear()
        orderItem.addAll(newOrders)
        notifyDataSetChanged()
    }
}