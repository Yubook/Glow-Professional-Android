package com.youbook.glowpros.ui.booking_details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.youbook.glowpros.ReviewImageItem
import com.youbook.glowpros.extension.loadingImage
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.ui.chat.ChatActivity
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.youbook.glowpros.R
import com.youbook.glowpros.databinding.ActivityBookingDetailsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.youbook.glowpros.utils.Utils.enable
import gun0912.tedimagepicker.util.ToastUtil.context

class BookingDetailsActivity : AppCompatActivity(), View.OnClickListener {
    private val barberDistance: Double = 0.0
    private lateinit var binding: ActivityBookingDetailsBinding
    private lateinit var viewModel: BookingDetailsViewModel
    private lateinit var imageAdapter: ReviewImageAdapter
    private val arrayListImage: ArrayList<ImageItem?> = ArrayList()
    private val imageList: ArrayList<ImageItem?> = ArrayList()
    var mapFragment: SupportMapFragment? = null
    private var myGoogleMap: GoogleMap? = null
    private var orderLat: String = ""
    private var orderLon: String = ""
    private var orderId: String = ""
    private var driverId: String = ""
    private var userId: String = ""
    private var userName: String = ""

    private var serviceRating: String = "0"
    private var valueRating: String = "0"
    private var hygieneRating: String = "0"
    private var reviewImages = ""
    private var groupId = ""
    private var isChatEnable = false
    var reviewImagesList: ArrayList<ReviewImageItem> = ArrayList<ReviewImageItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,
            BookingDetailsViewModelFactory(
                BookingDetailsRepository(
                    MyApi.getInstanceToken(
                        Prefrences.getPreferences(
                            this,
                            Constants.API_TOKEN
                        )!!
                    )
                )
            )
        ).get(BookingDetailsViewModel::class.java)

        mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        setOnClickListener()
        imageAdapter = ReviewImageAdapter(this, arrayListImage)
        binding.imageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.imageRecyclerView.adapter = imageAdapter

        getUserData()
    }

    private fun getUserData() {
        driverId = Prefrences.getPreferences(this, Constants.USER_ID)!!
        orderId = intent.getStringExtra(Constants.ORDER_ID)!!
        userId = intent.getStringExtra(Constants.USER_ID)!!
        userName = intent.getStringExtra(Constants.USER_NAME).toString()
        binding.tvBarberName.text = userName
        binding.tvBookedService.text = intent.getStringExtra(Constants.BOOKED_SERVICE)
        binding.tvTotalPrice.text = getString(R.string.pound_sign).plus(intent.getStringExtra(Constants.ORDER_PRICE))
        binding.tvBookingDate.text = intent.getStringExtra(Constants.BOOKED_DATE)
        orderLat = intent.getStringExtra(Constants.ORDER_LAT).toString()
        orderLon = intent.getStringExtra(Constants.ORDER_LON).toString()
        orderLon = intent.getStringExtra(Constants.ORDER_LON).toString()
        isChatEnable = intent.getBooleanExtra(Constants.IS_CHAT_ENABLE, false)
        groupId = intent.getStringExtra(Constants.GROUP_ID).toString()

        if(isChatEnable){
            binding.ivChat.enable(true, isOtpView = true)
        } else {
            binding.ivChat.enable(false, isOtpView = true)
        }

        val distance = "Get Directions - ".plus(String.format("%.2f", barberDistance)).plus(" mile")
        binding.tvDistance.text = distance

        reviewImages = intent.getStringExtra(Constants.REVIEW_IMAGES)!!

        val split = reviewImages.split(",")

        if (split.isNotEmpty()){
            split.forEach {
                if (it != ""){
                    arrayListImage.add(ImageItem(0, it, 0))
                }
            }
        }

        serviceRating = intent.getStringExtra(Constants.SERVICE_RATING).toString()
        valueRating = intent.getStringExtra(Constants.VALUE_RATING).toString()
        hygieneRating = intent.getStringExtra(Constants.HYGIENE_RATING).toString()

        if (arrayListImage.isEmpty()){
            binding.photos.text = getString(R.string.no_image_found)
            binding.photos.visibility = View.GONE
        } else {
            binding.photos.text = getString(R.string.photos)
            binding.photos.visibility = View.VISIBLE
        }

        if (serviceRating.isNotEmpty()){
            binding.relRatings.visibility = View.VISIBLE
            binding.hygieneRatingBar.rating = hygieneRating.toFloat()
            binding.valueRatingBar.rating = valueRating.toFloat()
            binding.serviceRatingBar.rating = serviceRating.toFloat()
        } else {
            binding.relRatings.visibility = View.GONE
        }

        mapFragment?.getMapAsync(callback)

        binding.tvAddress.text = Utils.getAddress(orderLat.toDouble(), orderLon.toDouble(), this)
        binding.tvBarberAddress.text = Utils.getAddress(orderLat.toDouble(), orderLon.toDouble(), this)

        loadingImage(
            this,
            Constants.STORAGE_URL.plus(intent.getStringExtra(Constants.USER_PROFILE_IMAGE)),
            binding.ivUserProfile,
            true
        )
    }

    private val callback = OnMapReadyCallback { googleMap ->
        var driverLatLon: LatLng? = null
        myGoogleMap = googleMap

        googleMap.clear()
        driverLatLon = LatLng(
            orderLat.toDouble(),
            orderLon.toDouble()
        )

        googleMap.uiSettings.isScrollGesturesEnabled = false
        googleMap.uiSettings.isZoomGesturesEnabled = false
        googleMap.uiSettings.isScrollGesturesEnabledDuringRotateOrZoom = false
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLatLon, 14F))
        googleMap.addMarker(
            MarkerOptions().position(driverLatLon)
        )
    }

    private fun setOnClickListener() {
        binding.ivBackButton.setOnClickListener(this)
        binding.relMap.setOnClickListener(this)
        binding.ivChat.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackButton -> finish()
            R.id.relMap -> {
                val uri =
                    "http://maps.google.com/maps?saddr=" + orderLat + "," + orderLon + "&daddr=" + Prefrences.getPreferences(this, Constants.LAT)!!.toString() + "," + Prefrences.getPreferences(this, Constants.LON).toString()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                startActivity(intent)
            }
            R.id.ivChat ->{
                openChatActivity()
            }
        }
    }

    private fun openChatActivity() {
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra(Constants.INTENT_KEY_CHAT_TITLE, userName)
        intent.putExtra(Constants.INTENT_KEY_CHAT_GROUP_ID, groupId)
        context.startActivity(intent)
    }
}