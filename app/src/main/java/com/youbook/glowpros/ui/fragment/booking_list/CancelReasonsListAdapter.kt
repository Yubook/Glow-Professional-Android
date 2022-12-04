package com.youbook.glowpros.ui.fragment.booking_list

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ItemOrderCancelReasonsBinding
import kotlinx.android.synthetic.main.item_order_cancel_reasons.view.*


class CancelReasonsListAdapter(private val context: Context) :
    RecyclerView.Adapter<CancelReasonsListAdapter.ViewHolder>(), View.OnClickListener {
    private var reasonList = ArrayList<ResultItem>()
    private var mSelectedItem = -1
    private var selectedReason = ""
    var otherEditTextReason: String = ""

    class ViewHolder(val binding: ItemOrderCancelReasonsBinding, var selectedReason: String) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItems(
            reasonsItem: ResultItem,
            position: Int,
            selectedPosition: Int
        ) {
            if ((selectedPosition == -1 && position == 0))
                itemView.rbCancelReason.setChecked(false)
            else {
                if (selectedPosition == position) {
                    itemView.rbCancelReason.setChecked(true)
                } else {
                    itemView.rbCancelReason.setChecked(false)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemOrderCancelReasonsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding, selectedReason)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reasons = reasonList[position]
        holder.bindItems(reasonList[position], position, mSelectedItem)
        holder.binding.tvReason.text = reasons.reason

        if (reasons.isSelected) {
            holder.binding.ivRadio.setImageDrawable(context.resources.getDrawable(R.drawable.ic_radio_selected))

            if (reasons.isSelected && reasons.reason.equals("Other")) {
                holder.binding.edtOtherReason.visibility = View.VISIBLE
            } else {
                holder.binding.edtOtherReason.visibility = View.GONE
            }
        } else {
            holder.binding.edtOtherReason.visibility = View.GONE
            holder.binding.ivRadio.setImageDrawable(context.resources.getDrawable(R.drawable.ic_radio_unselected))
        }
        /*holder.binding.rbCancelReason.setOnClickListener {
            mSelectedItem = holder.absoluteAdapterPosition
            notifyDataSetChanged()
        }*/

        val listener = View.OnClickListener {
            otherEditTextReason = ""
            if (reasons.isSingleSelection) {
                reasons.isSelected = !reasons.isSelected
            } else {
                for (i in reasonList.indices) {
                    reasonList.get(i).isSelected = false
                }
                reasonList.get(position).isSelected = true
            }
            /* if (reasons.selected && reasons.reason.equals("Other")){
                 holder.binding.edtOtherReason.visibility = View.VISIBLE
             } else {
                 holder.binding.edtOtherReason.visibility = View.GONE
             }*/
            notifyDataSetChanged()
        }

        holder.itemView.edtOtherReason.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    otherEditTextReason = "Please Add Any reason"
                } else {
                    otherEditTextReason = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        holder.itemView.setOnClickListener(listener)
    }

    override fun getItemCount(): Int = reasonList.size

    fun getSelectedItem(isSingleSelection: Boolean): java.util.ArrayList<ResultItem> {
        val selectedList: java.util.ArrayList<ResultItem> =
            java.util.ArrayList<ResultItem>()
        for (i in reasonList.indices) {
            if (isSingleSelection == reasonList[i].isSingleSelection) {
                if (reasonList[i].isSingleSelection) {
                    selectedList.add(reasonList[i])
                    return selectedList
                }
            } else {
                if (reasonList[i].isSelected) {
                    selectedList.add(reasonList[i])
                }
            }
        }
        return selectedList
    }

    fun updateList(newReasons: List<ResultItem>) {
        reasonList.clear()
        reasonList.addAll(newReasons)
        notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

}