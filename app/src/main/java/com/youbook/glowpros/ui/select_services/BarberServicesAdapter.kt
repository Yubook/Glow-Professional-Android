package com.youbook.glowpros.ui.select_services

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ItemSelectServicesBinding

class BarberServicesAdapter(
    private val context: Context,
    private val serviceItemClick: ((item: ResultItem, type: String) -> Unit)? = null
) :
    RecyclerView.Adapter<BarberServicesAdapter.ViewHolder>() {
    private val servicesItem = ArrayList<ResultItem>()
    private val selectedServiceItem = ArrayList<ResultItem>()
    private var selectedServices = ""

    private var isEdits: Boolean = false

    class ViewHolder(
        val binding: ItemSelectServicesBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemSelectServicesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = servicesItem[position]
        holder.binding.tvServiceName.text = service.name

        if (isEdits) {
            if (service.serviceAdded!!)
                holder.binding.ivServiceDelete.visibility = View.VISIBLE
            else
                holder.binding.ivServiceDelete.visibility = View.GONE
        } else {
            holder.binding.ivServiceDelete.visibility = View.GONE
        }

        if (service.isServiceSelected) {
            if (service.price.toString() != "null")
                holder.binding.edtServicePrice.setText(
                    context.getString(R.string.pound_sign).plus(service.price.toString())
                )
        } else {
            holder.binding.edtServicePrice.setText("")
        }

        if (service.isServiceSelected) {
            holder.binding.relService.setBackgroundResource(R.drawable.service_selected_bg)
            holder.binding.relPrice.visibility = View.VISIBLE
            holder.binding.tvServiceName.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.app_black
                )
            )
            holder.binding.priceBottomLine.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.app_black
                )
            )
            holder.binding.edtServicePrice.isEnabled = true
            holder.binding.edtServicePrice.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.app_black
                )
            )
        } else {
            holder.binding.relService.setBackgroundResource(R.drawable.service_unselected_bg)
            holder.binding.priceBottomLine.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.gray
                )
            )
            holder.binding.edtServicePrice.isEnabled = false
            holder.binding.edtServicePrice.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.gray
                )
            )
            if (service.serviceAdded!!) {
                holder.binding.relPrice.visibility = View.VISIBLE
                holder.binding.edtServicePrice.setText(
                    context.getString(R.string.pound_sign).plus(service.price.toString())
                )

            } else {
                holder.binding.relPrice.visibility = View.GONE
                holder.binding.edtServicePrice.setText("")
            }

            holder.binding.tvServiceName.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.unselected_rating_color
                )
            )
        }

        holder.itemView.setOnClickListener {
            service.isServiceSelected = !service.isServiceSelected
            serviceItemClick!!.invoke(service, "")
            notifyItemChanged(position)
        }

        holder.binding.ivServiceDelete.setOnClickListener {
            removeServicesDialog(context, service)
        }

        holder.binding.edtServicePrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    var p = s.toString().replace("£", "")
                    if (p.isNotEmpty() && !Character.isDigit(p[0])) {
                        val price = "0".plus(p)
                        service.servicesPrice = price.toDouble()
                        holder.binding.edtServicePrice.setText(
                            context.getString(R.string.pound_sign).plus(price)
                        )
                        holder.binding.edtServicePrice.setSelection(holder.binding.edtServicePrice.length())
                    } else {
                        if (p != null && p.isNotEmpty()){
                            service.servicesPrice = p.toDouble()
                            print("${service.servicesPrice}")
                        } else
                            service.servicesPrice = 0.0
                    }
                }
            }
        })
    }

    fun getSelectedServices(): List<ResultItem> {
        selectedServiceItem.clear()
        for (i in servicesItem.indices) {
            if (servicesItem[i].isServiceSelected) {
                selectedServiceItem.add(servicesItem[i])
            }
        }
        return selectedServiceItem
    }

    private fun removeServicesDialog(context: Context, service: ResultItem) {
        val dialog = Dialog(this.context, R.style.CustomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.enable_service_dialog)
        val tvRemove = dialog.findViewById(R.id.tvRemove) as TextView
        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView

        tvRemove.setOnClickListener {
            serviceItemClick!!.invoke(service, "Remove")
            dialog.cancel()
        }

        tvCancel.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }

    override fun getItemCount(): Int = servicesItem.size

    fun getList(): List<ResultItem> {
        return servicesItem
    }

    fun updateList(newServices: List<ResultItem>, isEdit: Boolean) {
        isEdits = isEdit
        servicesItem.clear()
        servicesItem.addAll(newServices)
        notifyDataSetChanged()
    }
}