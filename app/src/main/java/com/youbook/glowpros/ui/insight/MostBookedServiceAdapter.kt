package com.youbook.glowpros.ui.insight

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ItemMostBookedServicesBinding
import com.youbook.glowpros.utils.Utils

class MostBookedServiceAdapter (private val context: Context) :
    RecyclerView.Adapter<MostBookedServiceAdapter.ViewHolder>() {

    private val serviceList = ArrayList<DataItem>()

    class ViewHolder (val binding: ItemMostBookedServicesBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemBinding = ItemMostBookedServicesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = serviceList[position]

        var services: String? = ""
        var totalServicePrice: Double? = 0.0
        for (i in order.service!!.indices) {
            totalServicePrice = totalServicePrice?.plus(order.service[i]!!.servicePrice!!.toDouble())

            if (i == 0) {
                services += order.service[i]!!.serviceName.toString()
            } else {
                services += ", "
                services += order.service[i]!!.serviceName.toString()
            }
        }

        holder.binding.tvServiceName.text = services
        holder.binding.tvOrderDate.text = Utils.formatDate("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM, yyyy HH:mm a", order.created_at.toString() )

        holder.binding.tvServicePrice.text = context.getString(R.string.pound_sign).plus(" ${totalServicePrice.toString()}")

    }

    override fun getItemCount(): Int  = serviceList.size

    fun updateList(newNotification: ArrayList<DataItem>) {
        serviceList.clear()
        serviceList.addAll(newNotification)
        notifyDataSetChanged()
    }
}