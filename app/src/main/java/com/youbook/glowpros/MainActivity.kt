package com.youbook.glowpros

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import com.youbook.glowpros.extension.loadingImage
import com.youbook.glowpros.ui.fragment.booking_list.BookingListFragment
import com.youbook.glowpros.ui.fragment.chat_list.ChatListFragment
import com.youbook.glowpros.ui.fragment.dashboard.HomeFragment
import com.youbook.glowpros.ui.fragment.profile_frag.ProfileFragment
import com.youbook.glowpros.utils.Constants
import com.youbook.glowpros.utils.Prefrences
import com.youbook.glowpros.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding
    private var currentPage: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setOnClickListener()
        loadHomeFragment()
    }

    override fun onResume() {
        super.onResume()

        val image = Prefrences.getPreferences(this, Constants.USER_PROFILE_IMAGE)!!
        loadingImage(this, image, binding.ivNavProfileSelected, true)
        loadingImage(this, image, binding.ivNavProfileUnselected, true)
    }
    private fun setOnClickListener() {
        binding.relNavHome.setOnClickListener(this)
        binding.relNavChat.setOnClickListener(this)
        binding.relNavCalendar.setOnClickListener(this)
        binding.relNavProfile.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.relNavHome -> loadHomeFragment()
            R.id.relNavChat -> loadChatFragment()
            R.id.relNavCalendar -> loadBookingListFragment()
            R.id.relNavProfile -> loadProfileFragment()
        }
    }

    private fun loadHomeFragment() {
        if (currentPage != 1) {
            selectHomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrameContainer, HomeFragment())
                .commitAllowingStateLoss()
        }
    }

    private fun loadChatFragment() {
        if (currentPage != 2) {
            selectChatFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrameContainer, ChatListFragment())
                .commitAllowingStateLoss()
        }
    }

    private fun loadBookingListFragment() {
        if (currentPage != 3) {
            selectCalendarFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrameContainer, BookingListFragment())
                .commitAllowingStateLoss()
        }
    }

    private fun loadProfileFragment() {
        if (currentPage != 4) {
            selectProfileFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrameContainer, ProfileFragment())
                .commitAllowingStateLoss()
        }
    }

    private fun selectHomeFragment() {
        currentPage = 1
        binding.ivNavHome.setImageResource(R.drawable.ic_b_home_selected)
        binding.ivNavChat.setImageResource(R.drawable.ic_b_chat_unselected)
        binding.ivNavCalendar.setImageResource(R.drawable.ic_b_calendar_unselected)
        binding.ivNavProfileUnselected.visibility = View.VISIBLE
        binding.ivNavProfileSelected.visibility = View.GONE
    }

    private fun selectChatFragment() {
        currentPage = 2
        binding.ivNavHome.setImageResource(R.drawable.ic_b_home_unselected)
        binding.ivNavChat.setImageResource(R.drawable.ic_b_chat_selected)
        binding.ivNavCalendar.setImageResource(R.drawable.ic_b_calendar_unselected)
        binding.ivNavProfileUnselected.visibility = View.VISIBLE
        binding.ivNavProfileSelected.visibility = View.GONE
    }

    private fun selectCalendarFragment() {
        currentPage = 3
        binding.ivNavHome.setImageResource(R.drawable.ic_b_home_unselected)
        binding.ivNavChat.setImageResource(R.drawable.ic_b_chat_unselected)
        binding.ivNavCalendar.setImageResource(R.drawable.ic_b_calendar_selected)
        binding.ivNavProfileUnselected.visibility = View.VISIBLE
        binding.ivNavProfileSelected.visibility = View.GONE
    }

    private fun selectProfileFragment() {
        currentPage = 4
        binding.ivNavHome.setImageResource(R.drawable.ic_b_home_unselected)
        binding.ivNavChat.setImageResource(R.drawable.ic_b_chat_unselected)
        binding.ivNavCalendar.setImageResource(R.drawable.ic_b_calendar_unselected)
        binding.ivNavProfileUnselected.visibility = View.GONE
        binding.ivNavProfileSelected.visibility = View.VISIBLE
    }
}