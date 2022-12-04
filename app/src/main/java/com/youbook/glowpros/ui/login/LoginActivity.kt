package com.youbook.glowpros.ui.login

import CountryFlags.getCountryFlagByCountryCode
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivityLoginBinding
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.ui.code_verify.CodeVerifyActivity
import com.youbook.glowpros.ui.select_country.SelectCountryActivity
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils.toast
import java.io.Serializable

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel : LoginViewModel

    private lateinit var countrySpinnerAdapter: CountrySpinnerAdapter
    private var countryId: String? = null
    var countryList: ArrayList<ResultItem?> = ArrayList()
    var selectedCountry: String = "Select Country"
    var codeCountry: String = ""
    var isFirstTime: String? = null

    data class SelectedCountry (
        var selectedCountryFlag: String? = "GB",
        var selectedCountryCode: String? = "44",
        var selectedCountryId: String? = "232"
    ) : Serializable

    var selectedCountryData : SelectedCountry= SelectedCountry()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListener()
        spinnerListener()

        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(LoginRepository(MyApi.getInstance()))
        ).get(LoginViewModel::class.java)

        binding.ivCountryFlag.text =
            " ${getCountryFlagByCountryCode(SelectedCountry().selectedCountryFlag!!)}"
        binding.tvCountryCode.text = SelectedCountry().selectedCountryCode!!

        selectedCountryData = SelectedCountry()
    }

    private fun setOnClickListener() {
        binding.relGetStarted.setOnClickListener(this)
        binding.ivBackButton.setOnClickListener(this)
        binding.linSelectCountry.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.relGetStarted -> goToVerifyPhoneNumScreen()
            R.id.ivBackButton -> finish()
            R.id.linSelectCountry -> {
                val intent = Intent(this@LoginActivity, SelectCountryActivity::class.java)
                intent.putExtra(Constants.SelectedCountry,selectedCountryData)
                activityResultLaunch.launch(intent);
            }
        }
    }

    private var activityResultLaunch = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 123) {
            val data: Intent = result.data!!
            selectedCountryData = data.getSerializableExtra(Constants.SelectedCountry) as SelectedCountry
            binding.ivCountryFlag.text = " ${selectedCountryData.selectedCountryFlag}"
            binding.tvCountryCode.text = selectedCountryData.selectedCountryCode
        }
    }

    private fun goToVerifyPhoneNumScreen() {
        if (isValid()) {
            val intent = Intent(this, CodeVerifyActivity::class.java)
            intent.putExtra(Constants.USER_MOBILE_NO, "" + binding.edtPhoneNumber.text)
            intent.putExtra(Constants.SelectedCountry, selectedCountryData)
            startActivity(intent)
        }
    }

    private fun isValid(): Boolean {
        return when {
            TextUtils.isEmpty(binding.edtPhoneNumber.text.toString()) -> {
                this.toast(getString(R.string.err_empty_phone_number))
                false
            }
            binding.edtPhoneNumber.text.toString().length < 10 -> {
                this.toast(getString(R.string.err_valid_phone_number))
                false
            }
            else -> {
                true
            }
        }
    }

    private fun spinnerListener() {
        binding.spnCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("Nothing Selected")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedCountry = countryList[position]!!.phonecode!!
                codeCountry = countryList[position]!!.id!!.toString()

                Prefrences.savePreferencesString(this@LoginActivity, Constants.PREFS_CODE, codeCountry)
                Prefrences.savePreferencesString(this@LoginActivity, Constants.SELECTED_COUNTRY_CODE, selectedCountry)
            }

        }
    }
}