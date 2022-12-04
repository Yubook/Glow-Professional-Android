package com.youbook.glowpros.ui.splash

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.youbook.glowpros.MainActivity
import com.youbook.glowpros.R
import com.youbook.glowpros.ui.get_started_new.GetStartedNewActivity
import com.youbook.glowpros.ui.login.LoginActivity
import com.youbook.glowpros.ui.select_radius.SelectRadiusActivity
import com.youbook.glowpros.ui.select_services.SelectServicesActivity
import com.youbook.glowpros.ui.your_availability.YourAvailabilityActivity
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.ManagePermissions
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.youbook.glowpros.utils.Utils.openNoInternetConnectionActivity
import com.youbook.glowpros.utils.Utils.toast

class SplashActivity : AppCompatActivity() {
    private val TAG = "Permission"
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private var userId: String? = null
    private var isServiceAdded: String? = null
    private var isAvailabilityAdded: String? = null
    private var maxRadius: String? = null
    private var isFirstTime: String? = ""
    private val list = listOf<String>(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)
    }

    override fun onResume() {
        super.onResume()
        if (Utils.isConnected(this)){
            setUpNavigation()
        } else {
            openNoInternetConnectionActivity(this)
        }
    }

    private fun setUpNavigation() {
        userId = Prefrences.getPreferences(this, Constants.USER_ID)
        isServiceAdded = Prefrences.getPreferences(this, Constants.IS_SERVICE_ADDED)
        isAvailabilityAdded =
            Prefrences.getPreferences(this, Constants.IS_AVAILABILITY_ADDED)
        isFirstTime = Prefrences.getPreferences(this, Constants.IS_FIRST_TIME)
        maxRadius = Prefrences.getPreferences(this, Constants.MAX_RADIUS)

        if (isFirstTime!!.isEmpty()) {
            if (managePermissions.checkPermissions()) {
                Handler().postDelayed({
                    val intent = Intent(this, GetStartedNewActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2000)
            }

        } else {
            if (userId!!.isNotEmpty()) {
                if (isServiceAdded == "0") {
                    Handler().postDelayed({
                        val intent = Intent(this, SelectServicesActivity::class.java)
                        intent.putExtra(Constants.FROM, "SplashActivity")
                        startActivity(intent)
                        finish()
                    }, 2000)
                } else if (isAvailabilityAdded == "0") {
                    Handler().postDelayed({
                        val intent = Intent(this, YourAvailabilityActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                } else if (maxRadius == null || maxRadius == "0" || maxRadius.toString().isEmpty()) {
                    Handler().postDelayed({
                        val intent = Intent(this, SelectRadiusActivity::class.java)
                        intent.putExtra(Constants.FROM, "SplashActivity")
                        startActivity(intent)
                        finish()
                    }, 2000)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        if (managePermissions.checkPermissions()) {
                            Handler().postDelayed({
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }, 2000)
                        }
                }
            } else {
                if (managePermissions.checkPermissions()) {
                    Handler().postDelayed({
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)

                if (isPermissionsGranted) {
                    setUpNavigation()
                } else {
                    this.toast("Permissions denied.")
                    if (isFirstTime!!.isEmpty()) {
                        Handler().postDelayed({
                            val intent = Intent(this, GetStartedNewActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 2000)
                    } else {
                        if (userId!!.isNotEmpty()) {
                            Handler().postDelayed({
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }, 2000)
                        } else {

                            Handler().postDelayed({
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }, 2000)

                        }
                    }
                }
                return
            }
        }
    }
}