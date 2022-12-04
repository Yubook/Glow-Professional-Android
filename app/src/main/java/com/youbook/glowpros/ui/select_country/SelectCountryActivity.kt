package com.youbook.glowpros.ui.select_country

import CountryFlags.getCountryFlagByCountryCode
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eclipsa.fadedriver.ui.select_country.SelectCountryViewModelFactory
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivitySelectCountryBinding
import com.youbook.glowpros.extension.visible
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.ui.login.LoginActivity
import com.youbook.glowpros.ui.login.ResultItem
import com.youbook.glowpros.utils.Constants
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch
import java.util.stream.Collectors

class SelectCountryActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySelectCountryBinding
    private lateinit var viewModel: SelectCountryViewModel
    var countryList: ArrayList<ResultItem?> = ArrayList()

    private lateinit var countryAdapter: CountryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SelectCountryViewModelFactory(SelectCountryRepository(MyApi.getInstance()))
        ).get(SelectCountryViewModel::class.java)

        val selectedCountry: LoginActivity.SelectedCountry? =
            intent.getSerializableExtra(Constants.SelectedCountry) as LoginActivity.SelectedCountry??

        setUpRecyclerView(selectedCountry!!)
        getCountryCode()
        setListener()

        //Country response handler
        viewModel.countryCodeResponse.observe(this) {

            when (it) {
                is Resource.Success -> {
                    binding.progressBar.visible(false)
                    if (it.value.success!!) {
                        countryList.addAll(it.value.result!!)
                        countryAdapter.updateList(countryList)
                    } else {
                        ToastUtil.showToast(it.value.message!!)
                    }
                }
                is Resource.Failure -> {
                    binding.progressBar.visible(false)
                }
            }
        }

    }

    private fun setListener() {
        binding.ivBackButton.setOnClickListener(this)
        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @RequiresApi(Build.VERSION_CODES.N)
            override fun afterTextChanged(s: Editable?) {
                searchCountry(s.toString())
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun searchCountry(searchString: String) {
        var newList = countryList.stream().filter {
            it!!.name!!.contains(searchString, true)
        }.collect(Collectors.toList())

        countryAdapter.updateList(newList!! as ArrayList<ResultItem?>)
    }

    private fun setUpRecyclerView(selectedCountry: LoginActivity.SelectedCountry) {
        binding.countryRecyclerView.layoutManager = LinearLayoutManager(this)
        countryAdapter = CountryAdapter(this, selectedCountry) { item, type ->
            val selectedCountryData : LoginActivity.SelectedCountry = LoginActivity.SelectedCountry()

            selectedCountryData.selectedCountryCode = item.phonecode
            selectedCountryData.selectedCountryFlag = getCountryFlagByCountryCode(item.iso2!!)
            selectedCountryData.selectedCountryId = "${item.id}"
            intent.putExtra(Constants.SelectedCountry, selectedCountryData)
            setResult(123, intent)
            finish()
        }

        binding.countryRecyclerView.adapter = countryAdapter
    }

    private fun getCountryCode() {
        binding.progressBar.visible(true)
        viewModel.viewModelScope.launch {
            viewModel.countryCode()
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ivBackButton -> {
                onBackPressed()
            }
        }
    }
}