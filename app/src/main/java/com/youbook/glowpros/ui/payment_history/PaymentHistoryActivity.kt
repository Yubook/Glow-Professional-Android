package com.youbook.glowpros.ui.payment_history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivityPaymentHistoryBinding
import com.youbook.glowpros.extension.visible
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.youbook.glowpros.utils.Utils.hide
import kotlinx.coroutines.launch

class PaymentHistoryActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityPaymentHistoryBinding
    private lateinit var viewModel: PaymentHistoryViewModel
    private lateinit var adapter: PaymentHistoryAdapter
    private var userId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,
            PaymentHistoryModelFactory(
                PaymentHistoryRepository(
                    MyApi.getInstanceToken(Prefrences.getPreferences(this, Constants.API_TOKEN)!!)
                )
            )
        ).get(PaymentHistoryViewModel::class.java)

        setClickListener()
        binding.paymentHistoryRecyclerview.layoutManager = LinearLayoutManager(this)
        adapter = PaymentHistoryAdapter(this)
        binding.paymentHistoryRecyclerview.adapter = adapter

        userId = Prefrences.getPreferences(this, Constants.USER_ID)!!.toString()
        getBarberPaymentHistory()

        viewModel.userOrderResponse.observe(this) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()
                    if (it.value.success!!) {
                        binding.tvTotalExpense.text =
                            this.getText(R.string.pound_sign).toString().plus(" ")
                                .plus(it.value.result!!.totalExpense.toString())

                        if (it.value.result.order != null) {
                            adapter.updateList(
                                it.value.result.order.data!! as List<DataItem>/*.filter {
                                    orderItem -> !orderItem!!.isOrderComplete!!.toString().equals("0")
                            } as List<DataItem>*/
                            )
                        }

                        if (adapter.getList().isEmpty()) {
                            binding.tvNoData.visibility = View.VISIBLE
                        } else {
                            binding.tvNoData.visibility = View.GONE
                        }
                    } else {
                        Toast.makeText(this, it.value.message!!, Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    binding.progressBar.hide()
                    getBarberPaymentHistory()
                }
            }
        }
    }

    private fun getBarberPaymentHistory() {
        viewModel.viewModelScope.launch {
            viewModel.getBarberPaymentHistory(userId)
        }
    }

    private fun setClickListener() {
        binding.ivBackButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackButton -> finish()
        }
    }
}