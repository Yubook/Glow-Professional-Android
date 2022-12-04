package com.youbook.glowpros.ui.barber_terms_policy

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivityBarberTermsPolicyBinding
import com.youbook.glowpros.utils.Utils.snackbar
import kotlinx.coroutines.launch
import java.util.HashMap

class BarberTermsPolicyActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityBarberTermsPolicyBinding
    private lateinit var viewModel: BarberTermsViewModel
    private var driverId: String = ""
    private var termId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarberTermsPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            BarberTermsViewModelFactory(
                BarberTermsRepository(
                    MyApi.getInstanceToken(Prefrences.getPreferences(this, Constants.API_TOKEN)!!)
                )
            )
        ).get(BarberTermsViewModel::class.java)
        driverId = Prefrences.getPreferences(this, Constants.USER_ID)!!

        getBarberProfile()
        setOnClickListener()

        viewModel.getBarberProfileResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.success!!) {
                        if (it.value.result != null) {
                            termId = it.value.result.policyAndTerm!!.id.toString()
                            binding.edtTermsData.setText(it.value.result.policyAndTerm.content.toString())
                        }

                    } else {
                        binding.root.snackbar(it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                }
            }

        }

        viewModel.updateTermsResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.success!!) {
                        binding.root.snackbar(getString(R.string.terms_update_success_msg))
                    } else {
                        binding.root.snackbar(it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                }
            }
        }
    }

    private fun setOnClickListener() {
        binding.ivBackButton.setOnClickListener(this)
        binding.ivUpdateTerms.setOnClickListener(this)
    }

    private fun getBarberProfile() {
        viewModel.viewModelScope.launch {
            viewModel.getBarberProfile(driverId)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivBackButton ->{
                finish()
            }
            R.id.ivUpdateTerms ->{
                updateTermsForBarber()
            }
        }
    }

    private fun updateTermsForBarber() {

        val terms = binding.edtTermsData.text.toString().trim()
        if (terms.isEmpty()){
            binding.root.snackbar(getString(R.string.error_empty_terms_condition))
        } else {
            val params = HashMap<String, String>()
            params["type"] = "terms"
            params["content"] = terms
            params["id"] = termId

            viewModel.viewModelScope.launch {
                viewModel.updateTermsPolicy(params)
            }

        }
    }
}