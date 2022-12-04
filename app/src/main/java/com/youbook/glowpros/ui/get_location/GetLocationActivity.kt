package com.youbook.glowpros.ui.get_location

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivityGetLocationBinding
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.ManagePermissions
import com.youbook.glowpros.utils.Utils
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.android.synthetic.main.activity_get_location.*
import java.util.*


class GetLocationActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityGetLocationBinding
    var mCurrentLat: String? = null
    var mCurrentLon: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 111
    lateinit var placesClient: PlacesClient
    private var userCurrentAddress : String? = ""
    lateinit var mAdapter: AutoCompleteAdapter
    var mapFragment: SupportMapFragment? = null
    val list = listOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this@GetLocationActivity)
        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)

        // Initialize Places.
        var apiKey = getString(R.string.place_api_key)
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(this, apiKey)
        }
        placesClient = Places.createClient(this)

        userCurrentAddress = intent.getStringExtra(Constants.USER_ADDRESS)

        if (userCurrentAddress!!.isNotEmpty()){
            binding.autocomplete.setText(userCurrentAddress.toString())
        }

        setUpAutoCompleteTextView()
        clickListener()
        checkLocation()
        startLocationUpdates()
    }

    private fun setUpAutoCompleteTextView() {
        autocomplete.setThreshold(1)
        autocomplete.onItemClickListener = autocompleteClickListener
        mAdapter = AutoCompleteAdapter(this, placesClient)
        autocomplete.setAdapter(mAdapter)
    }

    private fun checkLocation() {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertLocation()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationUpdates()
    }

    private fun clickListener() {
        binding.tvCancel.setOnClickListener(this)
        binding.tvDone.setOnClickListener(this)
        binding.ivClear.setOnClickListener(this)
    }


    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        val currentLatLon: LatLng
        if (mCurrentLat != null && mCurrentLon != null) {
            currentLatLon = LatLng(mCurrentLat!!.toDouble(), mCurrentLon!!.toDouble())
        } else {
            currentLatLon = LatLng(51.4029, 0.1667)
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLon, 14F))

        mMap.setOnCameraIdleListener {
            //get latlng at the center by calling
            val midLatLng: LatLng = mMap.cameraPosition.target
            mCurrentLat = midLatLng.latitude.toString()
            mCurrentLon = midLatLng.longitude.toString()


            binding.autocomplete.setText(
                Utils.getAddress(midLatLng.latitude, midLatLng.longitude, this).toString()
            )
        }
    }

    fun animateMap(latitude: Double, longitude: Double){
        val currentLatLon = LatLng(latitude, longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLon, 14F))
    }

    var autocompleteClickListener: AdapterView.OnItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
        var item: AutocompletePrediction? = mAdapter.getItem(position)
        var placeID = item?.placeId
        var placeFields:  List<Place.Field> = Arrays.asList(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        var request: FetchPlaceRequest? = null
        if(placeID!=null){
            request = FetchPlaceRequest.builder(placeID, placeFields).build()
        }

        if(request!=null){
            placesClient.fetchPlace(request).addOnSuccessListener {
                //                responseView.setText(task.getPlace().getName() + "\n" + task.getPlace().getAddress());
                val place = it.place
                var stringBuilder = StringBuilder()
                stringBuilder.append("Name: ${place.name}\n")
                var queriedLocation: LatLng? = place.latLng
                stringBuilder.append("Latitude: ${queriedLocation?.latitude}\n")
                stringBuilder.append("Longitude: ${queriedLocation?.longitude}\n")
                stringBuilder.append("Address: ${place.address}\n")
                binding.autocomplete.setText(
                    Utils.getAddress(queriedLocation?.latitude!!, queriedLocation.longitude, this)
                        .toString()
                )
                mCurrentLat = queriedLocation.latitude.toString()
                mCurrentLon = queriedLocation.longitude.toString()

                animateMap(queriedLocation.latitude, queriedLocation.longitude)
                //searchDriver(queriedLocation?.latitude, queriedLocation?.longitude)
//                response_view.text = stringBuilder
                Log.i("TAG", "Called getPlaceById to get Place details for $placeID")

            }.addOnFailureListener {
                it.printStackTrace()
//                response_view.text = it.message
            }
        }
        hideKeyboard(binding.root)
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun showAlertLocation() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("Your location settings is set to Off, Please enable location to get location")
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

    override fun onResume() {
        super.onResume()
    }

    // Stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
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
                    val addresses: List<Address>?
                    val geoCoder = Geocoder(applicationContext, Locale.getDefault())

                    mCurrentLat = locationResult.lastLocation.latitude.toString()
                    mCurrentLon = locationResult.lastLocation.longitude.toString()

                    addresses = geoCoder.getFromLocation(
                        locationResult.lastLocation.latitude,
                        locationResult.lastLocation.longitude,
                        1
                    )
                    /*if (addresses != null && addresses.isNotEmpty()) {
                        val address: String = addresses[0].getAddressLine(0)
                        val city: String = addresses[0].locality
                        val state: String = addresses[0].adminArea
                        val country: String = addresses[0].countryName
                        val postalCode: String = addresses[0].postalCode
                        val knownName: String = addresses[0].featureName
                        Log.e("location", "$address $city $state $postalCode $country $knownName")
                    }*/
                }
            }


        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvCancel -> {
                setResult(RESULT_CANCELED)
                finish()
            }
            R.id.ivClear -> {
                binding.autocomplete.setText("")
            }
            R.id.tvDone -> {
                val intent = Intent()
                intent.putExtra("ADDRESS", binding.autocomplete.text.toString())
                intent.putExtra(Constants.LATEST_LAT, mCurrentLat)
                intent.putExtra(Constants.LATEST_LON, mCurrentLon)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}