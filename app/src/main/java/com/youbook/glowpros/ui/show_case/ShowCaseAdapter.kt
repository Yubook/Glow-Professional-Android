package com.youbook.glowpros.ui.show_case

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.databinding.ItemPortfolioBinding
import com.youbook.glowpros.extension.loadingImage
import com.youbook.glowpros.utils.Constants
import kotlin.collections.ArrayList

class ShowCaseAdapter(
    private val context: Context,
    private val portfolioList: ArrayList<DataItem?>,
    private val onItemClick: OnPortfolioClick,
    private val portfolioType: String
) :
    RecyclerView.Adapter<ShowCaseAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPortfolioBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemPortfolioBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = portfolioList[position]

        if(portfolioType == Constants.PORTFOLIO_TYPE_BARBER){
            holder.binding.relDelete.visibility = View.VISIBLE
        } else {
            holder.binding.relDelete.visibility = View.GONE
        }
        loadingImage(context, Constants.STORAGE_URL + item!!.path!!, holder.binding.ivPortfolioImage, false)

        holder.binding.relDelete.setOnClickListener{
            onItemClick.onClickDelete(item.id.toString(), position)
        }
    }


    override fun getItemCount(): Int = portfolioList.size

    fun getList(): ArrayList<DataItem?> {
        return portfolioList
    }

    fun updateList(newOrders: ArrayList<DataItem?>) {
        portfolioList.clear()
        portfolioList.addAll(newOrders)
        notifyDataSetChanged()
    }

}

interface OnPortfolioClick {
    fun onClickDelete(portfolioId: String?, position: Int)
}