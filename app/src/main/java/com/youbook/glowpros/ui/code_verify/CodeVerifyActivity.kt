package com.youbook.glowpros.ui.code_verify

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youbook.glowpros.MainActivity
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.ui.profile.ProfileActivity
import com.youbook.glowpros.ui.profile.ResultNew
import com.youbook.glowpros.ui.select_radius.SelectRadiusActivity
import com.youbook.glowpros.ui.select_services.SelectServicesActivity
import com.youbook.glowpros.ui.your_availability.YourAvailabilityActivity
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivityCodeVerifyBinding
import com.youbook.glowpros.extension.hideKeyboard
import com.youbook.glowpros.extension.snackBar
import com.youbook.glowpros.extension.visible
import com.youbook.glowpros.ui.login.LoginActivity
import com.youbook.glowpros.utils.Utils.enable
import com.youbook.glowpros.utils.Utils.hide


class CodeVerifyActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mVerificationId: String
    private val TAG = "CodeVerifyActivity"
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityCodeVerifyBinding
    private lateinit var viewModel: CodeVerifyViewModel
    private var mobileNumber: String? = null
    private var selectedCountry : LoginActivity.SelectedCountry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCodeVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,
            CodeVerifyViewModelFactory(CodeVerifyRepository(MyApi.getInstance()))
        ).get(CodeVerifyViewModel::class.java)
        binding.otpView.requestFocus()
        mobileNumber = intent.getStringExtra(Constants.USER_MOBILE_NO)
        selectedCountry = intent.getSerializableExtra(Constants.SelectedCountry) as LoginActivity.SelectedCountry
        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.PREFS_CODE,
            selectedCountry!!.selectedCountryId!!
        )
        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.SELECTED_COUNTRY_CODE,
            selectedCountry!!.selectedCountryCode!!
        )

        setUpFirebase()
        sendOtp()
        setOnClickListener()
        binding.otpView.enable(false, isOtpView = true)
        binding.progressBar.visibility = View.VISIBLE

        viewModel.loginResponse.observe(this) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()
                    if (it.value.success!!) {
                        if (it.value.message!!.contains("User Not Found")) {
                            goToAddProfileActivity()
                        } else {
                            if (it.value.result != null) {
                                savePreferences(it.value.result)
                                if (it.value.result.roleId == 2) {
                                    if (it.value.result.isServiceAdded == 0) {
                                        var intent = Intent(this, SelectServicesActivity::class.java)
                                        intent.putExtra(Constants.FROM, "CodeVerifyActivity")
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    } else if (it.value.result.isAvailability == 0) {
                                        var intent =
                                            Intent(this, YourAvailabilityActivity::class.java)
                                        intent.putExtra(Constants.FROM, "CodeVerifyActivity")
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    } else if (it.value.result.maxRadius!!.isEmpty()) {
                                        var intent = Intent(this, SelectRadiusActivity::class.java)
                                        intent.putExtra(Constants.FROM, "CodeVerifyActivity")
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        var intent = Intent(this, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                } else {
                                    ToastUtil.showToast("Can't register with User Mobile Number")
                                    finish()
                                }
                            }
                        }
                    } else {
                        if (it.value.message!!.contains("User Not Found")) {
                            goToAddProfileActivity()
                        } else {
                            binding.root.snackBar(it.value.message)
                        }

                    }
                }
                is Resource.Failure -> {
                    if (it.errorCode == 404) {
                        goToAddProfileActivity()
                    } else {
                        Utils.handleApiError(binding.root, it)
                    }
                }
            }
        }
    }

    private fun goToAddProfileActivity() {
        var intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra(Constants.IS_FIRST_TIME, "YES")
        intent.putExtra(Constants.USER_MOBILE_NO, mobileNumber)
        startActivity(intent)
        finish()
    }

    private fun contactAdmin(title:String, message:String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok") { _, _ ->
                finish()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.show()
    }

    private fun savePreferences(data: ResultNew) {
        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.USER_ID,
            data.id.toString()
        )
        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.ROLE_ID,
            data.roleId!!.toString()
        )
        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.USER_MOBILE_NO,
            data.mobile!!.toString()
        )
        Prefrences.savePreferencesString(this@CodeVerifyActivity, Constants.USER_NAME, data.name!!)
        Prefrences.savePreferencesString(this@CodeVerifyActivity, Constants.IS_SERVICE_ADDED, data.isServiceAdded!!.toString())
        Prefrences.savePreferencesString(this@CodeVerifyActivity, Constants.IS_AVAILABILITY_ADDED, data.isAvailability!!.toString())

        Prefrences.savePreferencesString(this@CodeVerifyActivity, Constants.API_TOKEN, data.token!!)
        Prefrences.savePreferencesString(this@CodeVerifyActivity, Constants.MAX_RADIUS, data.maxRadius.toString())
        Prefrences.savePreferencesString(this@CodeVerifyActivity, Constants.EMAIL_ID, data.email!!)
        Prefrences.savePreferencesString(this@CodeVerifyActivity, Constants.IS_BARBER_AVAILABLE, data.isBarberAvailable.toString())
        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.ADDRESS_LINE_1,
            data.addressLine1!!
        )

        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.ADDRESS_LINE_2,
            data.addressLine2!!
        )
        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.ADDRESS_LINE_2,
            data.addressLine2
        )

        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.POSTAL_CODE,
            data.postalCode!!
        )

        /*Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.STATE_ID,
            data.state!!.id.toString()
        )
        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.STATE_NAME,
            data.state.name!!
        )*/

        if(data.city?.id != null){
            Prefrences.savePreferencesString(
                this@CodeVerifyActivity,
                Constants.CITY_ID,
                data.city.id.toString()
            )
            Prefrences.savePreferencesString(
                this@CodeVerifyActivity,
                Constants.CITY_NAME,
                data.city.name!!
            )
        }


        Prefrences.savePreferencesString(this@CodeVerifyActivity, Constants.LAT, data.latitude!!)
        Prefrences.savePreferencesString(this@CodeVerifyActivity, Constants.LON, data.longitude!!)
        Prefrences.savePreferencesString(this@CodeVerifyActivity, Constants.LATEST_LAT, data.latestLatitude!!.toString()
        )
        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.LATEST_LON,
            data.latestLongitude!!.toString()
        )

        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.USER_GENDER,
            data.gender!!
        )

        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.DOCUMENT1_NAME,
            data.document1_name!!
        )

        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.DOCUMENT1_PATH,
            data.document1_path!!
        )

        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.DOCUMENT2_NAME,
            data.document2_name!!
        )

        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.DOCUMENT2_PATH,
            data.document2_path!!
        )


        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.PROFILE_APPROVED,
            data.profileApproved!!.toString()
        )
        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.IS_ACTIVE,
            data.isActive!!.toString()
        )
        Prefrences.savePreferencesString(
            this@CodeVerifyActivity,
            Constants.USER_PROFILE_IMAGE,
            Constants.STORAGE_URL.plus(data.profile!!)
        )

        Prefrences.savePreferencesString(this, Constants.IS_FIRST_TIME, "yes")

        viewModel.profilePic.value = data.profile
        viewModel.userName.value = data.name
    }

    private fun setUpFirebase() {
        mAuth = FirebaseAuth.getInstance()
        mAuth.setLanguageCode("En")
        binding.otpView.requestFocus()
    }

    private val mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.e(TAG, "onVerificationCompleted:$phoneAuthCredential")
                binding.progressBar.visibility = View.GONE
                binding.otpView.enable(true, isOtpView = true)
                binding.otpView.requestFocus()
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    binding.otpView.setText(code)
                } else {
                    signInWithPhoneAuthCredential(phoneAuthCredential)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG, "SMS sent ${e.message}")
                binding.progressBar.visibility = View.GONE
                Utils.showErrorDialog(this@CodeVerifyActivity, e.message.toString())
            }

            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                //super.onCodeSent(s, forceResendingToken);
                Log.e(TAG, "SMS sent $s")
                /* if (isLoading) hide()
                 binding.rlMain.setVisibility(View.VISIBLE)
                 binding.progressBar.setVisibility(View.GONE)*/
                binding.otpView.enable(true, isOtpView = true)
                binding.otpView.requestFocus()
                binding.progressBar.visibility = View.GONE
                setTimer()
                Utils.showKeyboard(this@CodeVerifyActivity)
                mVerificationId = s
            }
        }

    private fun setTimer() {
        Calendar.getInstance()

        object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.tvResendOtp.isEnabled = false
                binding.tvResendOtp.text =
                    "Seconds Remaining: " + getTimeFormat(millisUntilFinished / 1000)
            }

            override fun onFinish() {
                binding.tvResendOtp.isEnabled = true
                binding.tvResendOtp.text = getString(R.string.resend_code)
            }
        }.start()
    }

    private fun getTimeFormat(time: Long): String {
        val s = time.toString() + ""
        if (s.length == 2) return "00:$s"
        return if (s.length == 1) "00:0$s" else ""
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        Log.e(TAG, "signInWithCredential:success")
                        val user = task.result!!.user
                        loginUser()
                    } else {
                        Log.e(TAG, "signInWithCredential:fail.....")
                        binding.root.snackBar(task.exception!!.message!!)
                        //showInfoDialogCustom(task.exceptio
                        // n!!.message)
                    }
                })
    }

    private fun loginUser() {
        viewModel.viewModelScope.launch {
            viewModel.login(mobileNumber.toString(),selectedCountry!!.selectedCountryCode!!)
        }
    }

    private fun sendOtp() {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("${Constants.COUNTRY_CODE}$mobileNumber") // Phone number to verify
            .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun setOnClickListener() {
        binding.ivBackButton.setOnClickListener(this)
        binding.tvResendOtp.setOnClickListener(this)
        binding.otpView.setOtpCompletionListener {
            val code = binding.otpView.text.toString().trim()
            verifyVerificationCode(code)
            binding.root.hideKeyboard()
        }
    }

    private fun verifyVerificationCode(code: String) {
        if (code == null) return
        if (mVerificationId == null) return
        if (code == "" || mVerificationId == "") return
        //creating the credential
        val credential = PhoneAuthProvider.getCredential(mVerificationId, code)
        //signing the user
        signInWithPhoneAuthCredential(credential)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackButton -> finish()
            R.id.tvResendOtp -> resendOtp()
        }
    }

    private fun resendOtp() {
        binding.otpView.setText("")
        binding.progressBar.visibility = View.VISIBLE
        sendOtp()
    }
}