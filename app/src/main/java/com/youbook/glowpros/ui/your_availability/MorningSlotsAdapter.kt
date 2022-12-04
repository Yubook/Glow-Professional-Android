package com.youbook.glowpros.ui.your_availability

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ItemDriverSlotsBinding

class MorningSlotsAdapter(
    private val context: Context,
    private val serviceItemClick: ((item: ResultItem) -> Unit)? = null
) :
    RecyclerView.Adapter<MorningSlotsAdapter.ViewHolder>() {
    private val slotsItem = ArrayList<ResultItem>()
    private val selectedSlotsItem = ArrayList<String>()
    private var selectedServices = ""

    class ViewHolder(
        val binding: ItemDriverSlotsBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemDriverSlotsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = slotsItem[position]

        var timeHour = slot.time.toString().split(":")[0]

        if (timeHour == "0"){
            holder.binding.tvSlotTime.text = "12:" + slot.time.toString().split(":")[1]
        } else {
            holder.binding.tvSlotTime.text = slot.time.toString()
        }

        if (slot.isSelected){
            holder.binding.tvSlotTime.setTextColor(ContextCompat.getColor(this.context, R.color.app_black))
            holder.binding.tvSlotTime.background = ResourcesCompat.getDrawable(context.resources,
                R.drawable.drawable_black_rounded_corner_bg, null)
        } else {
            holder.binding.tvSlotTime.setTextColor(ContextCompat.getColor(this.context, R.color.app_black))
            holder.binding.tvSlotTime.background = null
        }

        holder.itemView.setOnClickListener {
            slot.isSelected = !slot.isSelected
            notifyDataSetChanged()
        }

    }

    fun getSelectedSlots(): List<String> {
        selectedSlotsItem.clear()
        for (i in slotsItem.indices) {
            if (slotsItem[i].isSelected) {
                selectedSlotsItem.add(slotsItem[i].id.toString())
            }
        }
        return selectedSlotsItem
    }

    override fun getItemCount(): Int = slotsItem.size

    fun getList(): List<ResultItem> {
        return slotsItem
    }

    fun updateList(newServices: List<ResultItem>) {
        slotsItem.clear()
        slotsItem.addAll(newServices)
        notifyDataSetChanged()
    }
}