package com.youbook.glowpros.ui.fragment.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewModelScope
import com.youbook.glowpros.R
import com.youbook.glowpros.base.BaseFragment
import com.youbook.glowpros.databinding.FragmentHomeBinding
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.ui.get_location.AutoCompleteAdapter
import com.youbook.glowpros.ui.notification.NotificationActivity
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.ManagePermissions
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import com.youbook.glowpros.utils.Utils.openNoInternetConnectionActivity
import gun0912.tedimagepicker.util.ToastUtil

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding, HomeRepository>(),
    View.OnClickListener {
    private val permissionsRequestCode = 111
    var mapFragment: SupportMapFragment? = null
    lateinit var placesClient: PlacesClient
    lateinit var mAdapter: AutoCompleteAdapter
    var mCurrentLat: String? = null
    var mCurrentLon: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var managePermissions: ManagePermissions
    private var myGoogleMap: GoogleMap? = null
    private var customMarkerView: View? = null
    private var icon : Bitmap? = null
    val list = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var driverLatLon: LatLng? = null

    private val callback = OnMapReadyCallback { googleMap ->

        myGoogleMap = googleMap

        googleMap.clear()
        driverLatLon = LatLng(
            Prefrences.getPreferences(requireContext(), Constants.LAT)!!.toDouble(),
            Prefrences.getPreferences(requireContext(), Constants.LON)!!.toDouble()
        )

        icon = BitmapFactory.decodeResource(
            requireContext().resources,
            R.drawable.ic_avtar_placeholder
        )

        binding.relMap.visibility = View.VISIBLE
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_map))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLatLon!!, 14F))

        
        googleMap.addMarker(
            MarkerOptions()
                .position(driverLatLon!!)
                .title(Prefrences.getPreferences(requireContext(), Constants.USER_NAME)!!)
                .snippet(Prefrences.getPreferences(requireContext(), Constants.USER_ADDRESS)!!)
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(getBitmapFromURL(Prefrences.getPreferences(requireContext(), Constants.USER_PROFILE_IMAGE).toString())!!)!!))
        )
    }

    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            icon
        }
    }

    private fun getMarkerBitmapFromView(bitmap: Bitmap): Bitmap? {
        val customMarkerView: View =
            (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!.inflate(
                R.layout.custom_barber_location_pin,
                null
            )
        val markerImageView = customMarkerView.findViewById<View>(R.id.ivBarberProfile) as ImageView
        markerImageView.setImageBitmap(bitmap)
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(0, 0,
            customMarkerView.measuredWidth,
            customMarkerView.measuredHeight
        )
        customMarkerView.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            customMarkerView.measuredWidth, customMarkerView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = customMarkerView.background
        drawable?.draw(canvas)
        customMarkerView.draw(canvas)
        return returnedBitmap
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        managePermissions = ManagePermissions(requireActivity(), list, permissionsRequestCode)
        setClickListener()

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Initialize Places.
        var apiKey = getString(R.string.place_api_key)
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey)
        }
        placesClient = Places.createClient(requireContext())
        setUpAutoCompleteTextView()

        val currentAddress = Utils.getAddress(
            Prefrences.getPreferences(
                requireContext(),
                Constants.LAT
            )!!.toDouble(),
            Prefrences.getPreferences(requireContext(), Constants.LON)!!.toDouble(),
            requireContext()
        )
        binding.tvBookingLocation.text = currentAddress

        getOffer()

        viewModel.getOfferResponse.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Success -> {

                    if (it.value.result != null) {
                        if (it.value.result.is_read!!) {
                            Prefrences.savePreferencesString(
                                requireContext(),
                                Constants.IS_AVAILABILITY_ADDED,
                                it.value.result.availability.toString()
                            )
                            binding.ivNotificationBadge.visibility = View.VISIBLE
                        } else {
                            binding.ivNotificationBadge.visibility = View.GONE
                        }
                    } else {
                        ToastUtil.showToast(it.value.message!!)
                    }

                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    getOffer()
                }
            }
        }

    }

    @SuppressLint("HardwareIds")
    private fun getOffer() {
        val params = HashMap<String, String>()
        val deviceId =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
        params["device_id"] = deviceId

        var firebaseToken =
            Prefrences.getPreferences(requireContext(), Constants.FIREBASE_TOKEN).toString()
        if (firebaseToken.isEmpty())
            firebaseToken = "abcdef"

        params["push_token"] = firebaseToken
        params["type"] = "2"

        if (mCurrentLon == null) {
            mCurrentLon = "0.0"
        }
        if (mCurrentLat == null) {
            mCurrentLat = "0.0"
        }
        params["latest_latitude"] = mCurrentLat!!
        params["latest_longitude"] = mCurrentLon!!

        viewModel.viewModelScope.launch {
            viewModel.getOffer(params)
        }
    }

    private fun setClickListener() {
        binding.tvBookAppointment.setOnClickListener(this)
        binding.relBarber.setOnClickListener(this)
        binding.relNotification.setOnClickListener(this)
        binding.ivClear.setOnClickListener(this)
    }

    private fun setUpAutoCompleteTextView() {
        autocomplete.threshold = 1
        autocomplete.onItemClickListener = autocompleteClickListener
        mAdapter = AutoCompleteAdapter(requireContext(), placesClient)
        autocomplete.setAdapter(mAdapter)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // Stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun enableLocationDialog() {
        val dialog = Dialog(requireContext(), R.style.CustomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_enable_location_permission)
        val tvDone = dialog.findViewById(R.id.tvDone) as TextView
        /* body.text = title
         val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
         val noBtn = dialog.findViewById(R.id.noBtn) as TextView*/

        tvDone.setOnClickListener {
            val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(myIntent)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun checkLocation() {
        val manager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLocationDialog()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocationUpdates()
    }

    private fun getLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
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

                    mCurrentLat = locationResult.lastLocation.latitude.toString()
                    mCurrentLon = locationResult.lastLocation.longitude.toString()
                    Prefrences.savePreferencesString(
                        requireContext(),
                        Constants.LAT,
                        mCurrentLat.toString()
                    )
                    Prefrences.savePreferencesString(
                        requireContext(),
                        Constants.LON,
                        mCurrentLon.toString()
                    )

                    getOffer()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Utils.isConnected(requireContext())){
            val token = arrayOf("")
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.isComplete) {
                        token[0] = task.result
                        Prefrences.savePreferencesString(
                            requireContext(),
                            Constants.FIREBASE_TOKEN,
                            token[0]
                        )
                    }
                }
            }
            checkLocation()
            binding.autocomplete.setText("")
            startLocationUpdates()
        } else {
            requireContext().openNoInternetConnectionActivity(requireContext())
        }
    }

    private fun startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (managePermissions.checkPermissions()) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
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

    var autocompleteClickListener: AdapterView.OnItemClickListener =
        AdapterView.OnItemClickListener { parent, view, position, id ->
            val item: AutocompletePrediction? = mAdapter.getItem(position)
            val placeID = item?.placeId
            val placeFields: List<Place.Field> = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
            var request: FetchPlaceRequest? = null
            if (placeID != null) {
                request = FetchPlaceRequest.builder(placeID, placeFields).build()
            }

            if (request != null) {
                placesClient.fetchPlace(request).addOnSuccessListener {
                    //                responseView.setText(task.getPlace().getName() + "\n" + task.getPlace().getAddress());
                    val place = it.place
                    val stringBuilder = StringBuilder()
                    stringBuilder.append("Name: ${place.name}\n")
                    val queriedLocation: LatLng? = place.latLng
                    stringBuilder.append("Latitude: ${queriedLocation?.latitude}\n")
                    stringBuilder.append("Longitude: ${queriedLocation?.longitude}\n")
                    stringBuilder.append("Address: ${place.address}\n")

                    mCurrentLat = queriedLocation?.latitude.toString()
                    mCurrentLon = queriedLocation?.longitude.toString()

//                    searchDriver(queriedLocation?.latitude, queriedLocation?.longitude)
//                response_view.text = stringBuilder
                    Log.i("TAG", "Called getPlaceById to get Place details for $placeID")

                }.addOnFailureListener {
                    it.printStackTrace()
//                response_view.text = it.message
                }
            }
            requireActivity().hideKeyboard(binding.root)
        }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun getViewModel() = HomeViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = HomeRepository(
        MyApi.getInstanceToken(
            Prefrences.getPreferences(requireContext(), Constants.API_TOKEN)!!
        )
    )

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.relNotification -> goToNotificationActivity()
            R.id.ivClear -> binding.autocomplete.setText("")
        }
    }

    private fun goToNotificationActivity() {
        startActivity(Intent(context, NotificationActivity::class.java))
    }

}