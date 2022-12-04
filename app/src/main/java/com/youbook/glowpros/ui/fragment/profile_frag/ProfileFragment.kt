package com.youbook.glowpros.ui.fragment.profile_frag

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import com.youbook.glowpros.BuildConfig
import com.youbook.glowpros.R
import com.youbook.glowpros.base.BaseFragment
import com.youbook.glowpros.databinding.FragmentProfileBinding
import com.youbook.glowpros.extension.loadingImage
import com.youbook.glowpros.extension.visible
import com.youbook.glowpros.network.MyApi
import com.youbook.glowpros.network.Resource
import com.youbook.glowpros.ui.barber_terms_policy.BarberTermsPolicyActivity
import com.youbook.glowpros.ui.chat.ChatActivity
import com.youbook.glowpros.ui.insight.InsightActivity
import com.youbook.glowpros.ui.login.LoginActivity
import com.youbook.glowpros.ui.notification.NotificationActivity
import com.youbook.glowpros.ui.payment_history.PaymentHistoryActivity
import com.youbook.glowpros.ui.profile.ProfileActivity
import com.youbook.glowpros.ui.review.ReviewsActivity
import com.youbook.glowpros.ui.select_radius.SelectRadiusActivity
import com.youbook.glowpros.ui.select_services.SelectServicesActivity
import com.youbook.glowpros.ui.show_case.ShowCaseActivity
import com.youbook.glowpros.ui.terms_privacy.TermsPrivacyActivity
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.utils.Utils
import com.youbook.glowpros.utils.Utils.hide
import com.youbook.glowpros.utils.Utils.snackbar
import kotlinx.coroutines.launch
import java.util.HashMap

class ProfileFragment :
    BaseFragment<ProfileFragViewModel, FragmentProfileBinding, ProfileFragRepository>(),
    View.OnClickListener {
    var userId: String? = null
    private var isBarberAvailable = false
    private var barberAvailability = "0"
    private var supportChatGroupId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListener()
        val name = Prefrences.getPreferences(requireContext(), Constants.USER_NAME)
        val emailAddress = Prefrences.getPreferences(requireContext(), Constants.EMAIL_ID)
        userId = Prefrences.getPreferences(requireContext(), Constants.USER_ID)
        barberAvailability = Prefrences.getPreferences(requireContext(), Constants.IS_BARBER_AVAILABLE)!!

        isBarberAvailable = barberAvailability != "0"

        binding.tvDriverName.text = name
        binding.tvDriverEmail.text = emailAddress
        binding.availabilitySwitch.isChecked = isBarberAvailable
        responseHandler()
    }

    private fun responseHandler() {
        viewModel.getOfferResponse.observe(requireActivity()) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()
                    if (it.value.result != null) {

                        if (it.value.result.customer_support_chat_id != null) {
                            supportChatGroupId = it.value.result.customer_support_chat_id
                            Prefrences.savePreferencesString(
                                requireContext(),
                                Constants.CUSTOMER_SUPPORT_CHAT_ID,
                                it.value.result.customer_support_chat_id
                            )
                        }
                        if (it.value.result.is_read!!) {
                            binding.ivNotificationBadge.visibility = View.VISIBLE
                        } else {
                            binding.ivNotificationBadge.visibility = View.GONE
                        }

                    } else {
                        binding.root.snackbar(it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    binding.progressBar.hide()
                }
                Resource.Loading -> {

                }
            }
        }

        viewModel.logoutResponse.observe(requireActivity()) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    binding.progressBar.hide()
                    if (it.value.success!!) {
                        Prefrences.clearPreferences(requireContext())
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        binding.root.snackbar(it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                    binding.progressBar.hide()
                }
                Resource.Loading -> {
                }
            }
        }

        viewModel.barberOnOffResponse.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.success!!) {
                        Prefrences.savePreferencesString(
                            requireContext(),
                            Constants.IS_BARBER_AVAILABLE,
                            if (isBarberAvailable) "1" else "0"
                        )
                        Log.d("TAG", "responseHandler: "+it.value)
                        binding.root.snackbar(it.value.message!!)
                    } else {
                        binding.root.snackbar(it.value.message!!)
                    }
                }
                is Resource.Failure -> Utils.handleApiError(binding.root, it) {
                }
                Resource.Loading -> {
                }
            }
        }

    }


    private fun setClickListener() {
        binding.tvEditProfile.setOnClickListener(this)
        binding.relInsight.setOnClickListener(this)
        binding.relLogout.setOnClickListener(this)
        binding.relNotification.setOnClickListener(this)
        binding.relWallet.setOnClickListener(this)
        binding.relPrivacy.setOnClickListener(this)
        binding.relTerms.setOnClickListener(this)
        binding.relRating.setOnClickListener(this)
        binding.relAboutUs.setOnClickListener(this)
        binding.relYourTerms.setOnClickListener(this)
        binding.relEditService.setOnClickListener(this)
        binding.relInviteFriend.setOnClickListener(this)
        binding.relCustomerSupport.setOnClickListener(this)
        binding.relEditRadius.setOnClickListener(this)
        binding.relShowCase.setOnClickListener(this)

        binding.availabilitySwitch.setOnClickListener{
            makeBarberAvailable(isBarberAvailable)
        }

        binding.availabilitySwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            isBarberAvailable = when (isChecked) {
                true -> true
                false -> false
            }
        }
    }

    private fun makeBarberAvailable(barberAvailable: Boolean) {
        viewModel.viewModelScope.launch {
            viewModel.barberOnOff(if (barberAvailable) 1 else 0)
        }
    }

    override fun getViewModel() = ProfileFragViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentProfileBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = ProfileFragRepository(
        MyApi.getInstanceToken(
            Prefrences.getPreferences(requireContext(), Constants.API_TOKEN)!!
        )
    )

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvEditProfile -> goToProfileActivity()
            R.id.relNotification -> goToNotificationActivity()
            R.id.relInsight -> goToInsightActivity()
            R.id.relRating -> goToReviewActivity()
            R.id.relPrivacy -> goToPrivacyPolicy("Privacy")
            R.id.relTerms -> goToPrivacyPolicy("Terms")
            R.id.relAboutUs -> goToPrivacyPolicy("AboutUs")
            R.id.relLogout -> openPopupForLogout()
            R.id.relWallet -> goToPaymentHistory()
            R.id.relYourTerms -> goToBarberTermsActivity()
            R.id.relInviteFriend -> shareApp()
            R.id.relEditService -> goToSelectServiceActivity()
            R.id.relCustomerSupport -> openChatWithAdmin()
            R.id.relEditRadius -> goToSelectRadiusActivity()
            R.id.relShowCase -> goToShowCaseActivity()
        }

    }

    private fun goToShowCaseActivity() {
        var intent = Intent(context, ShowCaseActivity::class.java)
        startActivity(intent)
    }

    private fun goToSelectRadiusActivity() {
        var intent = Intent(context, SelectRadiusActivity::class.java)
        intent.putExtra(Constants.FROM, "ProfileFragment")
        startActivity(intent)
    }

    private fun openChatWithAdmin() {
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra(Constants.INTENT_KEY_CHAT_TITLE, Constants.CUSTOMER_SUPPORT)
        intent.putExtra(Constants.INTENT_KEY_CHAT_GROUP_ID, supportChatGroupId)
        startActivity(intent)
    }

    private fun goToSelectServiceActivity() {
        var intent = Intent(requireContext(), SelectServicesActivity::class.java)
        intent.putExtra(Constants.FROM, "ProfileFragment")
        startActivity(intent)
    }

    private fun goToBarberTermsActivity() {
        startActivity(Intent(context, BarberTermsPolicyActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        val image = Prefrences.getPreferences(requireContext(), Constants.USER_PROFILE_IMAGE)
        loadingImage(requireContext(), image!!, binding.ivDriverProfile, true)
        getOffer()
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Fade")
            var shareMessage = """
            Fade Barber App

        Get the best deal to book your appointment with fade.

        Install App Now
            
        """
            shareMessage = """
        ${shareMessage}Download Android App
        https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
            
        Download iOS App 
        ${Constants.IOS_APP_LINK}
            """.trimIndent()
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "choose one"))
        } catch (e: Exception) {
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
        params["latest_latitude"] =
            Prefrences.getPreferences(requireContext(), Constants.LAT).toString()
        params["latest_longitude"] =
            Prefrences.getPreferences(requireContext(), Constants.LON).toString()

        viewModel.viewModelScope.launch {
            viewModel.getOffer(params)
        }
    }

    private fun goToInsightActivity() {
        startActivity(Intent(context, InsightActivity::class.java))
    }

    private fun goToPaymentHistory() {
        startActivity(Intent(context, PaymentHistoryActivity::class.java))
    }

    private fun goToReviewActivity() {
        startActivity(Intent(context, ReviewsActivity::class.java))
    }

    private fun goToNotificationActivity() {
        startActivity(Intent(context, NotificationActivity::class.java))
    }

    private fun goToProfileActivity() {
        startActivity(Intent(context, ProfileActivity::class.java))
    }

    private fun goToPrivacyPolicy(type: String) {
        val intent = Intent(context, TermsPrivacyActivity::class.java)
        intent.putExtra("Type", type)
        startActivity(intent)
    }

    private fun openPopupForLogout() {
        val dialog = Dialog(requireContext(), R.style.CustomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_logout)
        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
        val tvLogout = dialog.findViewById(R.id.tvLogout) as TextView
        /* body.text = title
         val yesBtn = dialog.findViewById(R.id.yesBtn) as Button
         val noBtn = dialog.findViewById(R.id.noBtn) as TextView*/

        tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        tvLogout.setOnClickListener {
            dialog.dismiss()
            logoutUser()
        }
        dialog.show()

    }

    private fun logoutUser() {
        viewModel.viewModelScope.launch {
            viewModel.logoutUser()
        }
    }


}