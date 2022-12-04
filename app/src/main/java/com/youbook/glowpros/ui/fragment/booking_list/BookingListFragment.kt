package com.youbook.glowpros.ui.fragment.booking_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.youbook.glowpros.base.BaseFragment
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.ui.fragment.booking_list.*
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.youbook.glowpros.utils.Utils.openNoInternetConnectionActivity
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.FragmentBookingListBinding
import com.youbook.glowpros.extension.visible
import com.youbook.glowpros.utils.Utils.hide
import kotlinx.coroutines.launch
import java.util.HashMap

class BookingListFragment :
    BaseFragment<BookingListViewModel, FragmentBookingListBinding, BookingListRepository>(),
    View.OnClickListener, OnItemClick {
    private var driver_id: String? = null
    private lateinit var adapter: BookingListAdapter
    private var previousOrderItem  = ArrayList<DataItem>()
    private var currentOrderItem  = ArrayList<DataItem>()
    private var futureOrderItem  = ArrayList<DataItem>()
    private var orderType : String= "Current"
    private var cancelReasonList = ArrayList<ResultItem>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setClickListener()
        driver_id = Prefrences.getPreferences(requireContext(), Constants.USER_ID)
        binding.bookingRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = BookingListAdapter(requireContext(), cancelReasonList, this)
        binding.bookingRecyclerview.adapter = adapter

        getCancelReason()

        viewModel.completeOrderResponse.observe(requireActivity()) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()
                    Log.d("TAG", "onViewCreated: ".plus(it.value))
                    if (it.value.success!!) {
                        Toast.makeText(context, "Order Successfully Completed!", Toast.LENGTH_SHORT)
                            .show()
                        getDriverOrders()
                    } else {
                        Utils.showErrorDialog(requireContext(), it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    binding.progressBar.hide()
                }
            }

        }

        viewModel.cancelOrderResponse.observe(requireActivity()) {
            binding.progressBar.visible(it is Resource.Loading)

            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()
                    Log.d("TAG", "onViewCreated: ".plus(it.value))

                    if (it.value.success!!) {
                        Toast.makeText(
                            context,
                            "Order Cancellation Successfully Done!",
                            Toast.LENGTH_SHORT
                        ).show()
                        getDriverOrders()
                    } else {
                        Utils.showErrorDialog(requireContext(), it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    binding.progressBar.hide()
                }
            }

        }

        viewModel.cancelReasonsResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.success!!) {
                        cancelReasonList.addAll(it.value.result!! as List<ResultItem>)
                        cancelReasonList.add(ResultItem("Other", 1, "", "", 123, false))
                        adapter.notifyDataSetChanged()
                    } else {
                        Utils.showErrorDialog(requireContext(), it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    getCancelReason()
                }
            }
        }

        viewModel.userOrderResponse.observe(requireActivity()) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()

                    if (it.value.success!!) {
                        if (orderType == Constants.ORDER_TYPE_FUTURE) {
                            if (it.value.result!!.next != null) {

                                previousOrderItem =
                                    it.value.result.next!!.data!!.filter { orderItem ->
                                        orderItem!!.isOrderComplete!!.toString() == "0"
                                    } as ArrayList<DataItem>

                                adapter.updateList(previousOrderItem, orderType)
                            }
                        } else if (orderType == Constants.ORDER_TYPE_CURRENT) {

                            if (it.value.result!!.today != null) {
                                currentOrderItem =
                                    it.value.result.today!!.data!!.filter { orderItem ->
                                        orderItem!!.isOrderComplete!!.toString() == "0"
                                    } as ArrayList<DataItem>

                                adapter.updateList(currentOrderItem, orderType)
                            }
                        } else {
                            if (it.value.result!!.previous != null) {
                                previousOrderItem =
                                    it.value.result.previous!!.data!! as ArrayList<DataItem>
                                adapter.updateList(previousOrderItem, orderType)
                            }
                        }

                        if (adapter.getList().isNullOrEmpty()) {
                            binding.tvNoData.visibility = View.VISIBLE
                        } else {
                            binding.tvNoData.visibility = View.GONE
                        }
                    } else {
                        Utils.showErrorDialog(requireContext(), it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    binding.progressBar.hide()
                    getDriverOrders()
                }
            }
        }

    }

    private fun getCancelReason() {
        viewModel.viewModelScope.launch {
            viewModel.getCancelReasons()
        }
    }

    private fun setClickListener() {
        binding.rbCurrent.setOnClickListener(this)
        binding.rbFuture.setOnClickListener(this)
        binding.rbPrevious.setOnClickListener(this)
    }

    override fun getViewModel() = BookingListViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentBookingListBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = BookingListRepository(
        MyApi.getInstanceToken(
            Prefrences.getPreferences(requireContext(), Constants.API_TOKEN)!!
        )
    )

    override fun onResume() {
        super.onResume()
        getDriverOrders()
    }

    private fun getDriverOrders() {
        if (Utils.isConnected(requireContext())){
            viewModel.viewModelScope.launch {
                viewModel.getDriverOrders()
            }
        } else {
            requireContext().openNoInternetConnectionActivity(requireContext())
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rbCurrent -> {
                binding.rbPrevious.isChecked = false
                binding.rbFuture.isChecked = false
                orderType = Constants.ORDER_TYPE_CURRENT
                getDriverOrders()
            }
            R.id.rbPrevious -> {
                binding.rbCurrent.isChecked = false
                binding.rbFuture.isChecked = false
                orderType = Constants.ORDER_TYPE_PREVIOUS
                getDriverOrders()
            }
            R.id.rbFuture -> {
                binding.rbPrevious.isChecked = false
                binding.rbCurrent.isChecked = false
                orderType = Constants.ORDER_TYPE_FUTURE
                getDriverOrders()
            }
        }
    }

    override fun onClick(reason: String?, orderId: String?, userId: String?) {
        cancelOrder(reason, orderId, userId)
    }

    override fun onCompleteOrder(orderId: String?) {
        makeOrderComplete(orderId)
    }

    private fun makeOrderComplete(orderId: String?) {
        val params = HashMap<String, String>()
        params["order_id"] = orderId!!

        viewModel.viewModelScope.launch {
            viewModel.completeOrder(params)
        }
    }

    private fun cancelOrder(reason: String?, orderId: String?, userId: String?) {
        val params = HashMap<String, String>()
        params["user_id"] = userId!!
        params["barber_id"] = driver_id!!
        params["order_id"] = orderId!!
        params["cancle_by"] = "barber"
        params["reason"] = reason.toString()

        viewModel.viewModelScope.launch {
            viewModel.cancelOrder(params)
        }
    }

}