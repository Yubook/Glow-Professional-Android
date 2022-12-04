package com.youbook.glowpros.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.fade.ui.chat.MessageDataItem
import com.youbook.glowpros.extension.loadingImage
import com.youbook.glowpros.utils.Utils
import com.youbook.glowpros.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.item_receiver.view.*
import java.text.SimpleDateFormat
import java.util.*


class ChattingAdapter(
    private val context: Context,
    private val messageList: ArrayList<MessageDataItem>,
    private val userId: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when (viewType) {
            1 -> {
                return SenderViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_sender,
                        parent,
                        false
                    )
                )
            }
            0 -> {
                return ReceiverViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_receiver,
                        parent,
                        false
                    )
                )
            }
            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (userId == "" + messageList[position].userId) 1 else 0
    }

    override fun getItemCount(): Int = messageList.size
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val list: MessageDataItem = messageList[position]
        val localTime: Calendar =
            Utils.getLocalTime(list.updatedAt, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")!!
        @SuppressLint("SimpleDateFormat") val time =
            SimpleDateFormat("hh:mm a").format(localTime.time)

        if (holder is SenderViewHolder) {
            holder.bind(list, context, time)
        }
        if (holder is ReceiverViewHolder) {
            holder.bind(list, context, time)
        }

    }

}

class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(list: MessageDataItem, context: Context, time: String) {
        if (list.header != "") {
            itemView.tvHeaderDate.visibility = View.VISIBLE
            itemView.tvHeaderDate.text = list.header
        } else {
            itemView.tvHeaderDate.visibility = View.GONE
        }

        if (list.type == 1) {
            // Text
            itemView.relMsgContainer.visibility = View.VISIBLE
            itemView.imageCard.visibility = View.GONE
            itemView.tvMsg.text = list.message

        } else if (list.type == 2) {
            //Image
            itemView.relMsgContainer.visibility = View.GONE
            itemView.imageCard.visibility = View.VISIBLE

            loadingImage(context, list.filename!!, itemView.ivImage, false)
            itemView.imageCard.setOnClickListener {
                openImageBottomSheet(list.filename!!, context)
            }
        }
        itemView.tvTime.text = time
    }

    private fun openImageBottomSheet(filename: String, context: Context) {
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(R.layout.custom_image_dialog)
        val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
        BottomSheetBehavior.from(bottomSheet!!).setState(BottomSheetBehavior.STATE_EXPANDED)
        val ivImage = dialog.findViewById<ImageView>(R.id.ivImage)
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        loadingImage(context, filename, ivImage!!, false)

        ivClose!!.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }
}

class ReceiverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(list: MessageDataItem, context: Context, time: String) {
        if (list.header != "") {
            itemView.tvHeaderDate.visibility = View.VISIBLE
            itemView.tvHeaderDate.text = list.header
        } else {
            itemView.tvHeaderDate.visibility = View.GONE
        }

        if (list.type == 1) {
            // Text
            itemView.relMsgContainer.visibility = View.VISIBLE
            itemView.imageCard.visibility = View.GONE
            itemView.tvMsg.text = list.message

        } else if (list.type == 2) {
            //Image
            itemView.relMsgContainer.visibility = View.GONE
            itemView.imageCard.visibility = View.VISIBLE

            loadingImage(context, list.filename!!, itemView.ivImage, false)

            itemView.imageCard.setOnClickListener {
                openImageBottomSheet(list.filename!!, context)
            }
        }
        itemView.tvTime.text = time
    }

    private fun openImageBottomSheet(filename: String, context: Context) {
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(R.layout.custom_image_dialog)
        val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
        BottomSheetBehavior.from(bottomSheet!!).setState(BottomSheetBehavior.STATE_EXPANDED)
        val ivImage = dialog.findViewById<ImageView>(R.id.ivImage)
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        loadingImage(context, filename, ivImage!!, false)

        ivClose!!.setOnClickListener{
            dialog.dismiss()
        }

        dialog.show()
    }
}
