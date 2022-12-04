package com.youbook.glowpros.ui.review

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ItemReviewBinding
import com.youbook.glowpros.extension.loadingImage
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReviewAdapter(private val context: Context) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {
    private val reviewList = ArrayList<ResultItem>()
    private lateinit var imageAdapter: ImageAdapter
    private val arrayListImage = java.util.ArrayList<String>()

    class ViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageRecyclerView: RecyclerView = itemView.findViewById(R.id.imageRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviewList[position]
        val localTime: Calendar =
            Utils.getLocalTime(review.created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")!!
        @SuppressLint("SimpleDateFormat") val reviewTime =
            SimpleDateFormat("dd MMM, yyyy hh:mm a").format(localTime.time)

        loadingImage(
            context,
            Constants.STORAGE_URL.plus(review.fromIdUser!!.image),
            holder.binding.ivDriverImage,
            true
        )
        holder.binding.tvDriverName.text = review.fromIdUser.name
        holder.binding.tvReviewDescription.text = review.message
        holder.binding.tvReviewTime.text = reviewTime
        holder.binding.driverReviewRatingBar.rating = review.rating!!.toFloat()
        val layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        layoutManager.initialPrefetchItemCount = review.image!!.size
        holder.imageRecyclerView.layoutManager = layoutManager
        val adapter = ReviewImageAdapter(context, review.image)
        holder.imageRecyclerView.adapter = adapter
    }


    override fun getItemCount(): Int = reviewList.size

    fun getList(): List<ResultItem> {
        return reviewList
    }

    fun updateList(newOrders: List<ResultItem>) {
        reviewList.clear()
        reviewList.addAll(newOrders)
        notifyDataSetChanged()
    }


}