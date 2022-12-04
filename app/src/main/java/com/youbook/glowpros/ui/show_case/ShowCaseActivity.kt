package com.youbook.glowpros.ui.show_case

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivityShowCaseBinding
import com.youbook.glowpros.extension.visible
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.ui.add_portfolio.AddPortfolioActivity
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.youbook.glowpros.utils.Utils.hide
import com.youbook.glowpros.utils.Utils.openNoInternetConnectionActivity
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch

class ShowCaseActivity : AppCompatActivity(), OnPortfolioClick, View.OnClickListener {
    private lateinit var binding: ActivityShowCaseBinding
    private lateinit var viewModel: ShowCaseViewModel
    private var barberPortfolioList: ArrayList<DataItem?> = ArrayList()
    private var userReviewList: ArrayList<DataItem?> = ArrayList()
    private lateinit var adapter: ShowCaseAdapter
    private var portfolioType: String = Constants.PORTFOLIO_TYPE_BARBER

    private var postPositionToDelete: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowCaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ShowCaseViewModelFactory(
                ShowCaseRepository(
                    MyApi.getInstanceToken(
                        Prefrences.getPreferences(
                            this,
                            Constants.API_TOKEN
                        )!!
                    )
                )
            )
        ).get(ShowCaseViewModel::class.java)

        setUpPortfolioRecyclerview()
        setOnClickListener()

        viewModel.barberPortfolioResponse.observe(this) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()
                    if (it.value.success!!) {
                        if (it.value.result != null) {
                            if (it.value.result.barberPortfolio != null) {
                                barberPortfolioList.addAll(it.value.result.barberPortfolio.data!!)
                                handleIsEmptyView(barberPortfolioList)
                            }

                            if (it.value.result.reviewPortfolio != null)
                                userReviewList.addAll(it.value.result.reviewPortfolio.data!!)
                        }
                    } else {
                        Utils.showErrorDialog(this, it.value.message!!)
                    }

                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    binding.progressBar.hide()
                }
            }
        }

        viewModel.deletePortfolioImageResponse.observe(this) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()
                    if (it.value.success!!) {
                        ToastUtil.showToast(it.value.message!!)
                        barberPortfolioList.removeAt(postPositionToDelete)
                        handleIsEmptyView(barberPortfolioList)
                        adapter.notifyDataSetChanged()
                    } else {
                        Utils.showErrorDialog(this, it.value.message!!)
                    }

                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    binding.progressBar.hide()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getBarberPortfolio()
    }
    private fun setOnClickListener() {
        binding.ivBackButton.setOnClickListener(this)
        binding.rbUserReview.setOnClickListener(this)
        binding.rbYourPortfolio.setOnClickListener(this)
        binding.fbAddPortfolio.setOnClickListener(this)
    }

    private fun setUpPortfolioRecyclerview() {
        binding.portfolioRecyclerview.layoutManager = GridLayoutManager(this, 2)
        adapter = ShowCaseAdapter(this, barberPortfolioList, this, portfolioType)
        binding.portfolioRecyclerview.adapter = adapter
    }

    private fun getBarberPortfolio() {
        userReviewList.clear()
        barberPortfolioList.clear()

        if (Utils.isConnected(this)) {
            viewModel.viewModelScope.launch {
                viewModel.getBarberPortfolio()
            }
        } else {
            openNoInternetConnectionActivity(this)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackButton -> {
                finish()
            }
            R.id.fbAddPortfolio ->{
                var intent = Intent(this, AddPortfolioActivity::class.java)
                startActivity(intent)
            }
            R.id.rbYourPortfolio -> {
                binding.rbUserReview.isChecked = false
                portfolioType = Constants.PORTFOLIO_TYPE_BARBER
                handleIsEmptyView(barberPortfolioList)
                adapter = ShowCaseAdapter(this, barberPortfolioList, this, portfolioType)
                binding.portfolioRecyclerview.adapter = adapter
            }
            R.id.rbUserReview -> {
                binding.rbYourPortfolio.isChecked = false
                portfolioType = Constants.PORTFOLIO_TYPE_USER
                handleIsEmptyView(userReviewList)
                adapter = ShowCaseAdapter(this, userReviewList, this, portfolioType)
                binding.portfolioRecyclerview.adapter = adapter
            }
        }
    }

    private fun handleIsEmptyView(portfolioList: java.util.ArrayList<DataItem?>) {
        if(portfolioList.isEmpty()){
            binding.tvNoData.visible(true)
        } else {
            binding.tvNoData.visible(false)
        }
    }


    override fun onClickDelete(portfolioId: String?, position: Int) {
        openPopupForDeletePortfolio(portfolioId, position)
    }

    private fun openPopupForDeletePortfolio(portfolioId: String?, position: Int) {
        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_delete_portfolio)
        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
        val tvDelete = dialog.findViewById(R.id.tvDelete) as TextView
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvDelete.setOnClickListener {
            postPositionToDelete = position
            dialog.dismiss()
            deletePortFolio(portfolioId)
        }
        dialog.show()

    }

    private fun deletePortFolio(portfolioId: String?) {
        if (Utils.isConnected(this)) {
            viewModel.viewModelScope.launch {
                viewModel.deletePortfolioImage(portfolioId!!)
            }
        } else {
            ToastUtil.showToast(Constants.NO_INTERNET_CONNECTION_MSG)
        }
    }
}