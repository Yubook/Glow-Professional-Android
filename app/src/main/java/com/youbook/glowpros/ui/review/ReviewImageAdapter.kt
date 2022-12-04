package com.youbook.glowpros.ui.review

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.extension.loadingImage
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ItemImageBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class ReviewImageAdapter(private val context: Context, private val arrayListImage: List<ImageItem?>) : RecyclerView.Adapter<ReviewImageAdapter.ViewHolder>() {

    class ViewHolder (val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        loadingImage(context, Constants.STORAGE_URL.plus(arrayListImage[position]!!.image!!), holder.binding.ivImage, false)

        holder.binding.cardItem.setOnClickListener {
            openImageBottomSheet(Constants.STORAGE_URL.plus(arrayListImage[position]!!.image!!), context)
        }
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

    override fun getItemCount(): Int = arrayListImage.size

    /*fun updateList(newImageList: List<String>) {
        arrayListImage.clear()
        arrayListImage.addAll(newImageList)
        notifyDataSetChanged()
    }*/
}