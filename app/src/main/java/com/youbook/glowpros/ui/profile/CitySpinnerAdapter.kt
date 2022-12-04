package com.youbook.glowpros.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CitySpinnerAdapter(private val context: Context,private val stateList: List<ResultItem?>) : BaseAdapter() {
    private var inflater : LayoutInflater? = LayoutInflater.from(context)


    override fun getCount(): Int {
        return stateList.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val view = inflater!!.inflate(com.youbook.glowpros.R.layout.item_spinner, null)
        val tv = view.findViewById<TextView>(com.youbook.glowpros.R.id.tvSpinnerName)
        tv.text = stateList[position]!!.name
        return view
    }
}