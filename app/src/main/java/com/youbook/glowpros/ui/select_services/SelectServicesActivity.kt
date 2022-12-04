package com.youbook.glowpros.ui.select_services

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivitySelectServicesBinding
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.ui.your_availability.YourAvailabilityActivity
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.youbook.glowpros.utils.Utils.enable
import com.youbook.glowpros.utils.Utils.snackbar
import kotlinx.coroutines.launch

class SelectServicesActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivitySelectServicesBinding
    private lateinit var viewModel: SelectServiceViewModel
    private lateinit var serviceList: ArrayList<ResultItem>
    private lateinit var barberServiceList: ArrayList<ResultItem>
    private lateinit var servicesAdapter: BarberServicesAdapter
    private var selectedServices = emptyList<ResultItem>()
    private var from: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectServicesBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(
            this,
            SelectServiceViewModelFactory(
                SelectServiceRepository(
                    MyApi.getInstanceToken(
                        Prefrences.getPreferences(
                            this,
                            Constants.API_TOKEN
                        )!!
                    )
                )
            )
        ).get(SelectServiceViewModel::class.java)

        setContentView(binding.root)
        setOnClickListener()
        setUpServicesRecyclerView()

        serviceList = ArrayList()
        barberServiceList = ArrayList()

        from = intent.getStringExtra(Constants.FROM)!!

        if (from == "ProfileFragment") {
            binding.ivBackButton.visibility = View.VISIBLE
            binding.tvContinue.text = getString(R.string.edit_service)
            binding.tvToolbarTitle.text = getString(R.string.edit_service)
            getBarberServices()
        } else {
            getServices()
            binding.tvContinue.text = getString(R.string.continue_text)
            binding.ivBackButton.visibility = View.GONE
            binding.tvToolbarTitle.text = getString(R.string.select_services)
        }

        viewModel.getServiceResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.result != null) {
                        serviceList = (it.value.result as ArrayList<ResultItem>?)!!
                        servicesAdapter.updateList(serviceList, false)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                }
            }
        }

        viewModel.barberServiceResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.result != null) {
                        barberServiceList = (it.value.result as ArrayList<ResultItem>?)!!
                        servicesAdapter.updateList(barberServiceList, true)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                }
            }
        }

        viewModel.addBarberServiceResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.success!!) {
                        if (from != "ProfileFragment") {
                            Prefrences.savePreferencesString(this, Constants.IS_SERVICE_ADDED, "1")
                            val intent = Intent(this, YourAvailabilityActivity::class.java)
                            startActivity(intent)
                        } else {
                            btnDisable()
                            binding.root.snackbar("Your Service updated Successfully done!")
                            finish()
                        }
                    } else {
                        binding.root.snackbar(it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                }
            }
        }

        viewModel.removeServiceResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.success!!) {
                        btnDisable()
                        binding.root.snackbar("Service removed Successfully done!")
                        getBarberServices()
                    } else {
                        binding.root.snackbar(it.value.message!!)
                    }

                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                }
            }
        }
    }

    private fun btnDisable() {
        binding.tvContinue.setBackgroundResource(R.drawable.drawable_dark_grey_rounded_corner_bg)
        binding.tvContinue.enable(false)
    }

    private fun getBarberServices() {
        viewModel.viewModelScope.launch {
            viewModel.getBarberService()
        }
    }

    private fun setUpServicesRecyclerView() {
        binding.serviceRecyclerview.layoutManager = LinearLayoutManager(this)
        servicesAdapter = BarberServicesAdapter(this) { item, type ->

            if (type == "Remove") {
                removeBarberService(item)
            } else {
                makeServiceSelected(item)
            }

        }
        binding.serviceRecyclerview.adapter = servicesAdapter
    }

    private fun removeBarberService(item: ResultItem) {
        viewModel.viewModelScope.launch {
            viewModel.removeBarberService(item.service_id!!)
        }
    }

    private fun makeServiceSelected(item: ResultItem) {
        selectedServices = servicesAdapter.getSelectedServices()

        if (selectedServices.isEmpty()) {
            btnDisable()
        } else {
            btnEnable()
        }
    }

    private fun btnEnable() {
        binding.tvContinue.setBackgroundResource(R.drawable.drawable_black_rounded_corner_bg)
        binding.tvContinue.enable(true)
    }

    private fun getServices() {
        viewModel.viewModelScope.launch {
            viewModel.getServices()
        }
    }

    private fun setOnClickListener() {
        binding.tvContinue.setOnClickListener(this)
        binding.ivBackButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvContinue -> goToYouAvailabilityScreen()
            R.id.ivBackButton -> finish()
        }
    }

    private fun goToYouAvailabilityScreen() {

        var isAllPriceAdded = false
        selectedServices = servicesAdapter.getSelectedServices()
        selectedServices.forEach {
            isAllPriceAdded = it.servicesPrice != 0.0
        }

        if (isAllPriceAdded) {
            val priceList: ArrayList<Double> = ArrayList()
            val serviceIdList: ArrayList<Int> = ArrayList()
            selectedServices.forEach {
                priceList.add(it.servicesPrice!!.toDouble())
                serviceIdList.add(it.id!!.toInt())
            }

            addBarberServices(priceList, serviceIdList)

        } else {
            binding.root.snackbar("Please enter selected Service price")
        }

    }

    private fun addBarberServices(priceList: List<Double>, serviceIdList: List<Int>) {
        viewModel.viewModelScope.launch {
            viewModel.addBarberService(serviceIdList, priceList)
        }
    }
}