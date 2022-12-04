package com.youbook.glowpros.ui.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youbook.glowpros.databinding.ActivityProfileBinding
import com.youbook.glowpros.extension.loadingImage
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.ui.login.LoginActivity
import com.youbook.glowpros.utils.*
import com.youbook.glowpros.utils.Utils.handleApiError
import com.youbook.glowpros.utils.Utils.toast
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.util.ToastUtil.context
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.youbook.glowpros.R
import com.youbook.glowpros.extension.visible
import com.youbook.glowpros.utils.Utils.hide
import com.youbook.glowpros.utils.Utils.snackbar


class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private var cityId: String? = null
    private var stateId: String? = null
    private val _tag = "ProfileActivity"
    private var mCurrentPhotoPath: String? = null
    private var barberIdPhotoPath: String? = null
    private var barberProofPhotoPath: String? = null
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    var mobileNumber: String? = null
    var isFirstTime: String? = null
    private val requestTakePhoto = 1
    private val requestGetPhoto = 2
    private val requestBarberIdPhoto = 3
    private val requestBarberProofPhoto = 4
    private var uriTemp: Uri? = null
    private val permissionsRequestCode = 111
    private val storagePermissionsRequestCode = 222
    var mCurrentLat: String? = null
    var mCurrentLon: String? = null
    var selectedGender: String? = "Male"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var managePermissions: ManagePermissions
    private lateinit var managePermissions2: ManagePermissions

    private var currentSelectedImage = ""
    val list = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val list2 = listOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var citySpinnerAdapter: CitySpinnerAdapter
    private lateinit var stateSpinnerAdapter: CitySpinnerAdapter

    var cityList: ArrayList<ResultItem?> = ArrayList()
    var stateList: ArrayList<ResultItem?> = ArrayList()

    var selectedCity: String = "Select City"
    var selectedState: String = "Select State"

    var selectedCityID: String = ""
    var selectedStateID: String = ""


    var driverImage = "driverImage"
    var driverIdPhoto = "driverIdPhoto"
    var driverProofPhoto = "driverProofPhoto"
    var barberProofPhotoName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managePermissions = ManagePermissions(this, list, permissionsRequestCode)
        managePermissions2 = ManagePermissions(this, list2, storagePermissionsRequestCode)
        binding.tvCountryCode.text = Prefrences.getPreferences(context, Constants.SELECTED_COUNTRY_CODE)
        cityList.add(ResultItem(0, "Select City", 0))
        stateList.add(ResultItem(0, "Select State", 0))
        viewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(
                ProfileRepository(
                    MyApi.getInstanceToken(
                        Prefrences.getPreferences(
                            this,
                            Constants.API_TOKEN
                        )!!
                    )
                )
            )
        ).get(ProfileViewModel::class.java)

        checkLocation()

        isFirstTime = intent.getStringExtra(Constants.IS_FIRST_TIME)
        mobileNumber = intent.getStringExtra(Constants.USER_MOBILE_NO)
        binding.edtPhoneNumber.setText(mobileNumber)
        startLocationUpdates()
        setClickListener()
        if (isFirstTime.equals("YES")) {
            binding.ivUserProfile.visibility = View.GONE
            binding.ivUserAddProfile.visibility = View.VISIBLE
            binding.ivBackButton.visibility = View.GONE
            binding.tvEditProfile.text = getString(R.string.save_profile)
            binding.edtEmailAddress.isEnabled = true
            binding.edtEmailAddress.setTextColor(ContextCompat.getColor(context, R.color.app_black))
            binding.edtPhoneNumber.isEnabled = true
            binding.edtBarberIdName.isEnabled = true
            binding.barberIdPhoto.isEnabled = true

        } else {
            binding.ivUserAddProfile.visibility = View.GONE
            binding.ivUserProfile.visibility = View.VISIBLE
            binding.ivBackButton.visibility = View.VISIBLE
            binding.tvEditProfile.text = getString(R.string.edt_profile)
            binding.edtEmailAddress.isEnabled = false
            binding.edtEmailAddress.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.unselected_rating_color
                )
            )
            binding.edtPhoneNumber.isEnabled = false
            binding.edtBarberIdName.isEnabled = false
            binding.barberIdPhoto.isEnabled = false

            setUserData()
        }

        // getState()
        getCity()

        spinnerListener()

        viewModel.updateProfileResponse.observe(this) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    if (it.value.result != null) {

                        binding.progressBar.hide()
                        if (isFirstTime.equals("YES")) {
                            openDialogUnderReview()
                        } else {
                            savePreferences(it.value.result)
                            binding.root.snackbar("Profile Updated Successfully!!")
                        }
                    } else {
                        binding.root.snackbar(it.value.message!!)
                    }
                }
                is Resource.Failure -> handleApiError(binding.root, it) {
//                    addProfileOrEditProfile()
                }
                Resource.Loading -> {

                }
            }

        }

        // City Response handler
        viewModel.getCityResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.success!!) {
                        cityList.addAll(it.value.result!!)
                        citySpinnerAdapter = CitySpinnerAdapter(context, cityList)
                        var indexOfFirst = 1
                        if (isFirstTime.equals("YES")) {
                            binding.spnCity.setSelection(0)
                            binding.spnCity.adapter = citySpinnerAdapter
                        } else {
                            indexOfFirst = cityList.indexOfFirst { resultItem ->
                                resultItem!!.id.toString() == cityId
                            }
                            binding.spnCity.adapter = citySpinnerAdapter
                            binding.spnCity.setSelection(indexOfFirst)
                        }
                    } else {
                        binding.root.snackbar(it.value.message!!)
                    }
                }
                is Resource.Failure -> handleApiError(binding.root, it) {
                }
                Resource.Loading -> {
                }
            }
        }

        // State Response handler
        /*viewModel.getStateResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.success!!) {
                        stateList.addAll(it.value.result!!)
                        stateSpinnerAdapter = CitySpinnerAdapter(context, stateList)
                        var indexOfFirst = 1
                        if (isFirstTime.equals("YES")) {
                            binding.spnState.setSelection(0)
                            binding.spnState.adapter = stateSpinnerAdapter
                        } else {

                            indexOfFirst = stateList.indexOfFirst { resultItem ->
                                resultItem!!.id.toString() == stateId
                            }
                            binding.spnState.setSelection(indexOfFirst)
                        }
                        binding.spnState.adapter = stateSpinnerAdapter

                    } else {
                        binding.root.snackbar(it.value.message!!)
                    }
                }
                is Resource.Failure -> handleApiError(binding.root, it) {
                }
                Resource.Loading -> {
                }
            }
        }*/
    }

    private fun openDialogUnderReview() {
        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_account_in_review)
        val tvOkay = dialog.findViewById(R.id.tvOkay) as TextView
        tvOkay.setOnClickListener {
            dialog.dismiss()
            val i = Intent(this@ProfileActivity, LoginActivity::class.java)
            i.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }
        dialog.show()
    }

    private fun spinnerListener() {
        /*binding.spnState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                println("Nothing Selected")
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedState = stateList[position]!!.name!!
                selectedStateID = "${stateList[position]!!.id!!}"
            }
        }*/

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
                selectedCity = cityList[position]!!.name!!
                selectedCityID = "${cityList[position]!!.id!!}"
            }

        }
    }

    private fun getState() {
        viewModel.viewModelScope.launch {
            viewModel.getStates()
        }
    }

    private fun getCity() {
        viewModel.viewModelScope.launch {
            viewModel.getCity(Prefrences.getPreferences(context, Constants.PREFS_CODE))
        }
    }

    private fun savePreferences(data: ResultNew) {

        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.USER_ID,
            data.id.toString()
        )
        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.ROLE_ID,
            data.roleId!!.toString()
        )
        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.USER_MOBILE_NO,
            data.mobile!!.toString()
        )

        if (data.token != null && data.token.isNotEmpty()) {
            Prefrences.savePreferencesString(this@ProfileActivity, Constants.API_TOKEN, data.token)
        }

        Prefrences.savePreferencesString(this@ProfileActivity, Constants.USER_NAME, data.name!!)
        //Prefrences.savePreferencesString(this@ProfileActivity, Constants.IS_BARBER_AVAILABLE, data.isBarberAvailable.toString())

        /*Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.VAN_NUMBER,
            data.van_number!!
        )*/

        Prefrences.savePreferencesString(this@ProfileActivity, Constants.EMAIL_ID, data.email!!)
        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.ADDRESS_LINE_1,
            data.addressLine1!!
        )

        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.POSTAL_CODE,
            data.postalCode!!
        )

        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.ADDRESS_LINE_2,
            data.addressLine2!!
        )
        Prefrences.savePreferencesString(this@ProfileActivity, Constants.LAT, data.latitude!!)
        Prefrences.savePreferencesString(this@ProfileActivity, Constants.LON, data.longitude!!)

        /*Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.STATE_ID,
            data.state!!.id.toString()
        )
        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.STATE_NAME,
            data.state.name!!
        )*/

        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.CITY_ID,
            data.city!!.id.toString()
        )
        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.CITY_NAME,
            data.city.name!!
        )
        /*Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.LATEST_LAT,
            data.latestLatitude!!
        )
        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.LATEST_LON,
            data.latestLongitude!!
        )*/

        Prefrences.savePreferencesString(this@ProfileActivity, Constants.USER_GENDER, data.gender!!)

        /*Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.PROFILE_APPROVED,
            data.profileApproved!!.toString()
        )
        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.IS_ACTIVE,
            data.isActive!!.toString()
        )*/

        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.USER_PROFILE_IMAGE,
            Constants.STORAGE_URL.plus(data.profile!!)
        )

        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.DOCUMENT1_NAME,
            data.document1_name!!
        )

        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.DOCUMENT1_PATH,
            data.document1_path!!
        )

        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.DOCUMENT2_NAME,
            data.document2_name!!
        )

        Prefrences.savePreferencesString(
            this@ProfileActivity,
            Constants.DOCUMENT2_PATH,
            data.document2_path!!
        )


        viewModel.profilePic.value = data.profile
        viewModel.userName.value = data.name

        Log.e(_tag, "savePreferences: " + Gson().toJson(data))
    }

    private fun setUserData() {
        val name = Prefrences.getPreferences(this, Constants.USER_NAME)
        val image = Prefrences.getPreferences(this, Constants.USER_PROFILE_IMAGE)
        val email = Prefrences.getPreferences(this, Constants.EMAIL_ID)
        val mobile = Prefrences.getPreferences(this, Constants.USER_MOBILE_NO)
        val addressLine1 = Prefrences.getPreferences(this, Constants.ADDRESS_LINE_1)
        val addressLine2 = Prefrences.getPreferences(this, Constants.ADDRESS_LINE_2)
        val vanNumber = Prefrences.getPreferences(this, Constants.VAN_NUMBER)
        val gender = Prefrences.getPreferences(this, Constants.USER_GENDER)
        val postalCode = Prefrences.getPreferences(this, Constants.POSTAL_CODE)
        cityId = Prefrences.getPreferences(this, Constants.CITY_ID)
        stateId = Prefrences.getPreferences(this, Constants.STATE_ID)

        var barberIdPhotoName = Prefrences.getPreferences(this, Constants.DOCUMENT1_NAME)
        var barberIdPhotoPath = Prefrences.getPreferences(this, Constants.DOCUMENT1_PATH)


        barberProofPhotoName = Prefrences.getPreferences(this, Constants.DOCUMENT2_NAME)!!
        var barberProofPhotoPath = Prefrences.getPreferences(this, Constants.DOCUMENT2_PATH)

        barberProofPhotoName.isEmpty().also {
//            binding.edtBarberProofName.isEnabled = it
//            binding.barberProofPhoto.isEnabled = it
        }

        if (barberProofPhotoName.isNotEmpty()) {
            binding.edtBarberProofName.setText(barberProofPhotoName)
            loadingImage(
                this,
                Constants.STORAGE_URL + barberProofPhotoPath!!,
                binding.ivBarberProofPhoto,
                false
            )
        }

        loadingImage(this, image!!, binding.ivUserProfile, true)
        loadingImage(
            this,
            Constants.STORAGE_URL + barberIdPhotoPath!!,
            binding.ivBarberIdPhoto,
            false
        )
        binding.edtUserName.setText(name)
        binding.edtEmailAddress.setText(email)
        binding.edtPhoneNumber.setText(mobile)
        binding.edtAddress1.setText(addressLine1)
        binding.edtAddress2.setText(addressLine2)
        binding.edtVanNumber.setText(vanNumber)
        binding.edtBarberIdName.setText(barberIdPhotoName)
        binding.edtPostalCode.setText(postalCode)

        if (gender.equals("male")) {
            binding.rbMale.isChecked = true
        } else {
            binding.rbFemale.isChecked = true
        }

        viewModel.profilePic.value = image
        viewModel.userName.value = name

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackButton -> finish()
            R.id.ivUserProfile -> {
                currentSelectedImage = driverImage
                if (managePermissions2.checkPermissions()) {
                    chooseImage(currentSelectedImage)
                }
            }

            R.id.ivUserAddProfile ->{
                ivUserProfile.visibility = View.VISIBLE
                currentSelectedImage = driverImage
                if (managePermissions2.checkPermissions()) {
                    chooseImage(currentSelectedImage)
                }
            }
            R.id.tvEditProfile -> {
                addProfileOrEditProfile()
            }
            R.id.barberIdPhoto -> {
                currentSelectedImage = driverIdPhoto
                if (managePermissions2.checkPermissions()) {
                    chooseImage(currentSelectedImage)
                }
            }

            R.id.barberProofPhoto -> {
                currentSelectedImage = driverProofPhoto
                if (managePermissions2.checkPermissions()) {
                    chooseImage(currentSelectedImage)
                }
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val address = data!!.getStringExtra("ADDRESS").toString()
                mCurrentLat = data.getStringExtra(Constants.LATEST_LAT).toString()
                mCurrentLon = data.getStringExtra(Constants.LATEST_LON).toString()
                //binding.edtAddress.setText(address)
            }
        }

    private fun checkLocation() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertLocation()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (managePermissions.checkPermissions()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null /* Looper */
                )
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)
                if (isPermissionsGranted) {
                    startLocationUpdates()
                } else {
                    this.toast("Permissions denied.")
                }
                return
            }
            storagePermissionsRequestCode -> {
                chooseImage(currentSelectedImage)
            }
        }
    }

    // Stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun showAlertLocation() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Your location settings is set to Off, Please enable location to use this application")
        dialog.setPositiveButton("Settings") { _, _ ->
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(myIntent)
        }
        dialog.setNegativeButton("Cancel") { _, _ ->
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()

    }

    private fun getLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f //170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //according to your app
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    /*val location = locationResult.lastLocation
                    Log.e("location", location.toString())*/
                    val geoCoder = Geocoder(applicationContext, Locale.getDefault())

                    mCurrentLat = locationResult.lastLocation.latitude.toString()
                    mCurrentLon = locationResult.lastLocation.longitude.toString()
                }
            }
        }
    }

    private fun setClickListener() {
        binding.ivBackButton.setOnClickListener(this)
        binding.ivUserProfile.setOnClickListener(this)
        binding.ivUserAddProfile.setOnClickListener(this)
        binding.tvEditProfile.setOnClickListener(this)

        binding.barberProofPhoto.setOnClickListener(this)
        binding.barberIdPhoto.setOnClickListener(this)

        binding.rgGender.setOnCheckedChangeListener { _, checkedId ->
            var radioButton = findViewById<View>(checkedId) as RadioButton

            selectedGender = radioButton.text.toString()
        }
    }

    private fun addProfileOrEditProfile() {

        if (binding.tvEditProfile.text.equals("Edit Profile")) {

            val params = HashMap<String, String>()
            val parts = ArrayList<MultipartBody.Part>()
            var body: MultipartBody.Part? = null
            val name = binding.edtUserName.text.toString().trim()
            val addressLine1 = binding.edtAddress1.text.toString().trim()
            val addressLine2 = binding.edtAddress2.text.toString().trim()
            val barberPhotoIdName = binding.edtBarberIdName.text.toString().trim()
            val barberProofPhotoName = binding.edtBarberProofName.text.toString().trim()
            val postalCode = binding.edtPostalCode.text.toString().trim()
//            selectedStateID = Prefrences.getPreferences(this, Constants.STATE_ID)!!
            selectedCityID = Prefrences.getPreferences(this, Constants.CITY_ID)!!
            selectedGender = Prefrences.getPreferences(this, Constants.USER_GENDER)!!

            /// For edit profile ...
            if (name.isEmpty()) {
                binding.root.snackbar(getString(R.string.empty_name))
            } else if (addressLine1.isEmpty()) {
                binding.root.snackbar(getString(R.string.error_empty_address_line_1))
            }/* else if (selectedState == "Select State") {
                binding.root.snackbar("Please select state")
            }*/ else if (selectedCity == "Select City") {
                binding.root.snackbar("Please select city")
            } else {

                if (Prefrences.getPreferences(this, Constants.DOCUMENT2_NAME)!!.isEmpty()) {
                    if (barberProofPhotoPath != null) {
                        val imagePath: String =
                            Utils.compressImage(this@ProfileActivity, barberProofPhotoPath!!)
                                .toString()
                        body = prepareFilePart("document_2", imagePath)
                        parts.add(body!!)
                        params["document_2_name"] = barberProofPhotoName
                    }
                }

                if (mCurrentPhotoPath != null) {
                    val imagePath: String =
                        Utils.compressImage(this@ProfileActivity, mCurrentPhotoPath!!).toString()
                    body = prepareFilePart("profile", imagePath)
                    parts.add(body!!)
                }

                params["name"] = name
                params["address_line_1"] = addressLine1
                params["address_line_2"] = addressLine2
                params["latitude"] = mCurrentLat!!
                params["longitude"] = mCurrentLon!!
                params["latitude"] = mCurrentLat!!
                params["longitude"] = mCurrentLon!!

//                params["state"] = selectedStateID
                params["city"] = selectedCityID
                params["postal_code"] = postalCode

                params["gender"] = selectedGender!!.toLowerCase(Locale.ROOT)

                if(parts.isEmpty()){
                    viewModel.viewModelScope.launch {
                        viewModel.updateProfileWithoutPhoto(params)
                    }
                } else {
                    viewModel.viewModelScope.launch {
                        viewModel.updateProfile(parts, params)
                    }
                }
            }

        } else {
            val params = HashMap<String, String>()
            val parts = ArrayList<MultipartBody.Part>()
            var body: MultipartBody.Part? = null
            val name = binding.edtUserName.text.toString().trim()
            val email = binding.edtEmailAddress.text.toString().trim()
            val addressLine1 = binding.edtAddress1.text.toString().trim()
            val addressLine2 = binding.edtAddress2.text.toString().trim()
            val barberPhotoIdName = binding.edtBarberIdName.text.toString().trim()
            val barberProofPhotoName = binding.edtBarberProofName.text.toString().trim()
            val postalCode = binding.edtPostalCode.text.toString().trim()

            /*if (mCurrentPhotoPath == null) {
                binding.root.snackbar("Please select Profile image")
            } else*/
            if (name.isEmpty()) {
                binding.root.snackbar(getString(R.string.empty_name))
            } else if (addressLine1.isEmpty()) {
                binding.root.snackbar(getString(R.string.error_empty_address_line_1))
            } else if (email.isEmpty() || !Utils.isEmailValid(email)) {
                binding.root.snackbar(getString(R.string.error_empty_email))
            } else if (barberIdPhotoPath == null) {
                binding.root.snackbar("Please select Barber Photo ID")
            } else if (barberPhotoIdName.isEmpty()) {
                binding.root.snackbar("Please enter Barber Photo ID name")
            } /*else if (selectedState == "Select State") {
                binding.root.snackbar("Please select state")
            }*/ else if (selectedCity == "Select City") {
                binding.root.snackbar("Please select city")
            }
            else if (barberProofPhotoPath != null) {
                if (barberProofPhotoName.isEmpty()) {
                    binding.root.snackbar("Please enter Barber Proof name")
                } else {
                    if (mCurrentPhotoPath != null) {
                        val imagePath: String =
                            Utils.compressImage(this@ProfileActivity, mCurrentPhotoPath!!).toString()
                        body = prepareFilePart("profile", imagePath)
                        parts.add(body!!)
                    }

                    if (barberIdPhotoPath != null) {
                        val imagePath: String =
                            Utils.compressImage(this@ProfileActivity, barberIdPhotoPath!!).toString()
                        body = prepareFilePart("document_1", imagePath)
                        parts.add(body!!)
                    }

                    if (barberProofPhotoPath != null) {
                        val imagePath: String =
                            Utils.compressImage(this@ProfileActivity, barberProofPhotoPath!!).toString()
                        body = prepareFilePart("document_2", imagePath)
                        parts.add(body!!)
                        params["document_2_name"] = barberProofPhotoName
                    }

                    params["phone_code"] = Prefrences.getPreferences(context, Constants.SELECTED_COUNTRY_CODE)!!
                    params["country_id"] = Prefrences.getPreferences(context, Constants.PREFS_CODE)!!
                    params["mobile"] = mobileNumber.toString()
                    params["name"] = name
                    params["email"] = email
                    params["gender"] = selectedGender.toString().lowercase(Locale.getDefault())
                    params["document_1_name"] = barberPhotoIdName
                    params["address_line_1"] = addressLine1
                    if (addressLine2.isNotEmpty()) {
                        params["addressLine2"] = addressLine2
                    }
                    params["latitude"] = mCurrentLat!!
                    params["longitude"] = mCurrentLon!!

//                    params["state"] = selectedStateID
                    params["city"] = selectedCityID
                    params["postal_code"] = postalCode


                    viewModel.viewModelScope.launch {
                        viewModel.addProfile(parts, params)
                    }
                }
            }
        }

    }

    private fun chooseImage(imageFor: String) {
        val btnSheet = layoutInflater.inflate(R.layout.choose_image_dialog, null)
        val dialog = BottomSheetDialog(this)
        val tvCaptureImage: TextView = btnSheet.findViewById(R.id.tvCaptureImage)
        val tvUploadImage: TextView = btnSheet.findViewById(R.id.tvUploadImage)
        dialog.setContentView(btnSheet)
        tvCaptureImage.setOnClickListener {
            takePicture(imageFor)
            dialog.cancel()
        }
        tvUploadImage.setOnClickListener {
            TedImagePicker.with(this)
                .start { uri -> showSingleImage(uri, imageFor) }
            dialog.cancel()
        }
        dialog.show()
    }

    private fun showSingleImage(uri: Uri, imageFor: String) {

        when (imageFor) {
            driverImage -> {
                mCurrentPhotoPath = FileUtil.getPath(context, uri)
                Glide.with(context)
                    .load(mCurrentPhotoPath)
                    .into(binding.ivUserProfile)
            }
            driverIdPhoto -> {
                barberIdPhotoPath = FileUtil.getPath(context, uri)
                Glide.with(context)
                    .load(barberIdPhotoPath)
                    .into(binding.ivBarberIdPhoto)
            }
            driverProofPhoto -> {
                barberProofPhotoPath = FileUtil.getPath(context, uri)
                Glide.with(context)
                    .load(barberProofPhotoPath)
                    .into(binding.ivBarberProofPhoto)
            }
        }

    }

    private fun takePicture(imageFor: String) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            photoFile = createImageFile()
            takePictureIntent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".provider", photoFile
                )
            )

            startActivityForResult(takePictureIntent, requestTakePhoto)
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        var storageDir: File? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            storageDir = File(
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                Constants.DirectoryName
            )
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                storageDir = File(
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                    Constants.DirectoryName
                )
            }
        }

        if (!storageDir!!.exists()) {
            val mkdir = storageDir.mkdirs()
            if (!mkdir) {
                Log.e("TAG", "Directory creation failed.")
            } else {
                Log.e("TAG", "Directory created.")
            }
        }
        val file = File(storageDir.path.toString() + File.separator + imageFileName)
        when (currentSelectedImage) {
            driverImage -> {
                mCurrentPhotoPath = file.absolutePath
            }
            driverIdPhoto -> {
                barberIdPhotoPath = file.absolutePath
            }
            driverProofPhoto -> {
                barberProofPhotoPath = file.absolutePath
            }
        }

        return file
    }

    private fun prepareFilePart(partName: String, fileUri: String): MultipartBody.Part? {
        val file = File(fileUri)
        val requestFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                requestTakePhoto -> {

                    when (currentSelectedImage) {
                        driverImage -> {
                            val f = File(mCurrentPhotoPath!!)
                            uriTemp = FileProvider.getUriForFile(
                                this,
                                applicationContext.packageName + ".provider",
                                f
                            )
                            cropImage(uriTemp!!)
                        }
                        driverIdPhoto -> {
                            val f = File(barberIdPhotoPath!!)
                            uriTemp = FileProvider.getUriForFile(
                                this,
                                applicationContext.packageName + ".provider",
                                f
                            )
                            cropImage(uriTemp!!)
                        }
                        driverProofPhoto -> {
                            val f = File(barberProofPhotoPath!!)
                            uriTemp = FileProvider.getUriForFile(
                                this,
                                applicationContext.packageName + ".provider",
                                f
                            )
                            cropImage(uriTemp!!)
                        }
                    }

                }

                requestGetPhoto -> {
                    uriTemp = data?.data

                    cropImage(uriTemp!!)
                }

                requestBarberIdPhoto -> {
                    uriTemp = data?.data

                    cropImage(uriTemp!!)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)

                    when (currentSelectedImage) {
                        driverImage -> {
                            mCurrentPhotoPath = Utils.getRealPathFromURI(this, result.uri)
                            val file = File(mCurrentPhotoPath!!)
                            Glide.with(application)
                                .load(file)
                                .placeholder(R.drawable.ic_avtar_placeholder)
                                .error(R.drawable.ic_avtar_placeholder)
                                .into(binding.ivUserProfile)
                        }
                        driverIdPhoto -> {
                            barberIdPhotoPath = Utils.getRealPathFromURI(this, result.uri)
                            val file = File(barberIdPhotoPath!!)
                            Glide.with(application)
                                .load(file)
                                .placeholder(R.drawable.ic_avtar_placeholder)
                                .error(R.drawable.ic_avtar_placeholder)
                                .into(binding.ivBarberIdPhoto)
                        }
                        driverProofPhoto -> {
                            barberProofPhotoPath = Utils.getRealPathFromURI(this, result.uri)
                            val file = File(barberProofPhotoPath!!)
                            Glide.with(application)
                                .load(file)
                                .placeholder(R.drawable.ic_avtar_placeholder)
                                .error(R.drawable.ic_avtar_placeholder)
                                .into(binding.ivBarberProofPhoto)
                        }
                    }

                }
            }
        }
    }

    private fun cropImage(uriTemp: Uri) {

        if (currentSelectedImage == driverImage) {
            CropImage.activity(uriTemp)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this)
        } else {
            CropImage.activity(uriTemp)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
        }

    }
}