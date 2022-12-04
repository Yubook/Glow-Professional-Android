package com.youbook.glowpros.ui.review

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.databinding.ItemImageBinding
import com.youbook.glowpros.extension.loadingImage

class ImageAdapter (private val context: Context, private val arrayListImage : ArrayList<String>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

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

        loadingImage(context, arrayListImage[position], holder.binding.ivImage, false)

    }

    override fun getItemCount(): Int = arrayListImage.size

    /*fun updateList(newImageList: List<String>) {
        arrayListImage.clear()
        arrayListImage.addAll(newImageList)
        notifyDataSetChanged()
    }*/
}