package com.youbook.glowpros.ui.select_radius

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youbook.glowpros.MainActivity
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivitySelectRadiusBinding
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.youbook.glowpros.extension.snackBar
import com.youbook.glowpros.extension.visible
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class SelectRadiusActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener,
    GoogleMap.OnMapLoadedCallback {
    lateinit var binding: ActivitySelectRadiusBinding
    private lateinit var viewModel: SelectRadiusViewModel
    private var mMap: GoogleMap? = null
    private var circle: Circle? = null
    private var icon: Bitmap? = null
    val list = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val metersInMile = 1609.344
    var barberLat: Double? = null
    var barberLon: Double? = null
    private var mapFragment: SupportMapFragment? = null
    var selectedMiles: Double = 1.0
    var alreadySelectedRadius: String = "0"
    var from: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectRadiusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,
            SelectRadiusViewModelFactory(
                SelectRadiusRepository(
                    MyApi.getInstanceToken(
                        Prefrences.getPreferences(
                            this,
                            Constants.API_TOKEN
                        )!!
                    )
                )
            )
        ).get(SelectRadiusViewModel::class.java)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)
        setOnClickListener()

        if (intent.getStringExtra(Constants.FROM) != null)
            from = intent.getStringExtra(Constants.FROM)!!
        if(Prefrences.getPreferences(this, Constants.LAT)!=""){
            barberLat = Prefrences.getPreferences(this, Constants.LAT)!!.toDouble()
            barberLon = Prefrences.getPreferences(this, Constants.LON)!!.toDouble()
        }

        icon = BitmapFactory.decodeResource(
            resources,
            R.drawable.ic_avtar_placeholder
        )

        if (from == "ProfileFragment") {
            binding.ivBackButton.visible(true)
            binding.tvToolbarTitle.text = getString(R.string.edit_radius)
            alreadySelectedRadius = Prefrences.getPreferences(this, Constants.MAX_RADIUS)!!
            selectedMiles = alreadySelectedRadius.toDouble()
            binding.radiusSeekbar.progress = alreadySelectedRadius.toInt()
        } else {
            binding.ivBackButton.visible(false)
            binding.tvToolbarTitle.text = getString(R.string.select_radius)
        }

        viewModel.selectRadiusResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.success!!) {
                        Prefrences.savePreferencesString(
                            this,
                            Constants.MAX_RADIUS,
                            selectedMiles.toInt().toString()
                        )
                        if (from == "ProfileFragment") {
                            finish()
                        } else {
                            var intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        ToastUtil.showToast(getString(R.string.barber_radius_added_success_msg))

                    } else {
                        binding.root.snackBar(it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                }
            }
        }
    }

    private fun drawCircle(latitude: Double, longitude: Double, radius: Double) {
        val circleOptions = CircleOptions()
            .center(LatLng(latitude, longitude))
            .radius(radius)
            .strokeWidth(2.0f)
            .strokeColor(ContextCompat.getColor(this, R.color.app_black))
            .fillColor(ContextCompat.getColor(this, R.color.gray_transparent))
        circle?.remove() // Remove old circle.
        if (mMap != null) {
            circle = mMap!!.addCircle(circleOptions) // Draw new circle.
        }
    }

    private fun setOnClickListener() {

        binding.tvContinue.setOnClickListener(this)
        binding.ivBackButton.setOnClickListener(this)
        binding.radiusSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                selectedMiles = progress.toDouble()
                drawCircle(barberLat!!, barberLon!!, milesToMeters(selectedMiles))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun milesToMeters(miles: Double): Double {
        return miles * metersInMile
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.setOnMapLoadedCallback(this)
        val driverLatLon: LatLng = if (barberLat != null && barberLon != null) {
            LatLng(barberLat!!.toDouble(), barberLon!!.toDouble())
        } else {
            LatLng(51.4029, 0.1667)
        }

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLon, 12F))

        googleMap.addMarker(
            MarkerOptions()
                .position(driverLatLon)
                .title(Prefrences.getPreferences(this, Constants.USER_NAME)!!)
                .snippet(Prefrences.getPreferences(this, Constants.USER_ADDRESS)!!)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getMarkerBitmapFromView(
                            getBitmapFromURL(
                                Prefrences.getPreferences(this, Constants.USER_PROFILE_IMAGE)
                                    .toString()
                            )!!
                        )!!
                    )
                )
        )

        drawCircle(barberLat!!, barberLon!!, milesToMeters(selectedMiles))
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
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!.inflate(
                R.layout.custom_barber_location_pin,
                null
            )
        val markerImageView = customMarkerView.findViewById<View>(R.id.ivBarberProfile) as ImageView
        markerImageView.setImageBitmap(bitmap)
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        customMarkerView.layout(
            0,
            0,
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvContinue -> addBarberRadius()
            R.id.ivBackButton -> finish()
        }
    }

    private fun addBarberRadius() {
        val params = HashMap<String, String>()
        params["min_radius"] = "1"
        params["max_radius"] = selectedMiles.toInt().toString()
        viewModel.viewModelScope.launch {
            viewModel.addBarberService(params)
        }
    }

    override fun onMapLoaded() {
        drawCircle(barberLat!!, barberLon!!, milesToMeters(selectedMiles))
        Log.d("TAG", "onMapLoaded:zzzzzzzzzzzzzzzzzzzzzzzz ")
    }
}