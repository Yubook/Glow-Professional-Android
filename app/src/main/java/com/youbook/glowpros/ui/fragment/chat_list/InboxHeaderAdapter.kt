package com.youbook.glowpros.ui.fragment.chat_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.chat_models.HeaderInbox
import com.youbook.glowpros.chat_models.UsersItem
import com.youbook.glowpros.databinding.ItemHeaderInboxBinding
import com.youbook.glowpros.utils.Utils

class InboxHeaderAdapter(private val context: Context) : RecyclerView.Adapter<InboxHeaderAdapter.ViewHolder>() {
    private val arrayList: ArrayList<HeaderInbox> = ArrayList()

    class ViewHolder(val binding: ItemHeaderInboxBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemHeaderInboxBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list: HeaderInbox = arrayList[position]
        holder.binding.tvHeader.text = Utils.capitalize(list.title)
        holder.binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = InboxAdapter(context, list.driverList as List<UsersItem>)
        holder.binding.recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun getItemCount() : Int = arrayList.size

    fun updateList(newOrders: List<HeaderInbox>) {
        arrayList.clear()
        arrayList.addAll(newOrders)
        notifyDataSetChanged()
    }
}