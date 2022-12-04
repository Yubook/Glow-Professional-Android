package com.youbook.glowpros.ui.add_portfolio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.R

import com.youbook.glowpros.extension.loadingImage

class AddPortfolioAdapter(
    private val context: Context,
    private val portfolioList: ArrayList<String>,
    private val onItemClick: OnPortfolioClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ADD_PORTFOLIO = 1
    val IMAGE = 2

    private inner class AddPortfolioViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var relAddPortFolio: RelativeLayout = itemView.findViewById(R.id.relAddPortFolio)
        fun bind(position: Int) {
            val recyclerViewModel = portfolioList[position]

            relAddPortFolio.setOnClickListener {
                onItemClick.addPortFolio()
            }
        }
    }

    private inner class PortfolioViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var relDelete: RelativeLayout = itemView.findViewById(R.id.relDelete)
        var ivPortfolioImage : ImageView = itemView.findViewById(R.id.ivPortfolioImage)
        fun bind(position: Int) {
            val recyclerViewModel = portfolioList[position]
            loadingImage(context, portfolioList[position], ivPortfolioImage, false)
            relDelete.setOnClickListener {
                portfolioList.removeAt(position)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ADD_PORTFOLIO) {
            return AddPortfolioViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_add_portfolio, parent, false)
            )
        }
        return PortfolioViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_portfolio, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            (holder as AddPortfolioViewHolder).bind(position)
        } else {
            (holder as PortfolioViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0){
            ADD_PORTFOLIO
        } else {
            IMAGE
        }
    }

    override fun getItemCount(): Int = portfolioList.size

    fun getList(): ArrayList<String> {
        return portfolioList
    }

    fun updateList(newOrders: ArrayList<String>) {
        portfolioList.clear()
        portfolioList.addAll(newOrders)
        notifyDataSetChanged()
    }

}

interface OnPortfolioClick {
    fun onClickDelete(portfolioId: String?, position: Int)
    fun addPortFolio()
}