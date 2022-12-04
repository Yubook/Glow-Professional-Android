package com.youbook.glowpros.ui.your_availability

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.databinding.ItemDriverSlotsBinding

class EveningSlotsAdapter(
    private val context: Context,
    private val serviceItemClick: ((item: ResultItem) -> Unit)? = null
) :
    RecyclerView.Adapter<EveningSlotsAdapter.ViewHolder>() {
    private val servicesItem = ArrayList<ResultItem>()
    private val selectedServiceItem = ArrayList<ResultItem>()
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
        val service = servicesItem[position]
    }

    fun getSelectedServices(): List<ResultItem> {
        selectedServiceItem.clear()
        for (i in servicesItem.indices) {
            /*if (servicesItem[i].isServiceSelected) {
                selectedServiceItem.add(servicesItem[i])
            }*/
        }
        return selectedServiceItem
    }

    override fun getItemCount(): Int = servicesItem.size

    fun getList(): List<ResultItem> {
        return servicesItem
    }

    fun updateList(newServices: List<ResultItem>) {
        servicesItem.clear()
        servicesItem.addAll(newServices)
        notifyDataSetChanged()
    }
}