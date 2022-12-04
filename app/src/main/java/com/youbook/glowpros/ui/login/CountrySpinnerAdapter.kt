package com.youbook.glowpros.ui.login

import CountryFlags.getCountryFlagByCountryCode
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.youbook.glowpros.R

class CountrySpinnerAdapter (private val context: Context, private val stateList: List<ResultItem?>) : BaseAdapter() {
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

        val view = inflater!!.inflate(R.layout.item_spinner_country_code, null)
        val tv = view.findViewById<TextView>(R.id.tvCountryName)
        val iv = view.findViewById<TextView>(R.id.ivCountryFlag)
        tv.text = stateList[position]!!.phonecode
        iv.text =  getCountryFlagByCountryCode(stateList[position]!!.iso2!!)
        return view
    }


}