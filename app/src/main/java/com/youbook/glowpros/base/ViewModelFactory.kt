package com.youbook.glowpros.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.youbook.glowpros.repository.BaseRepository
import com.youbook.glowpros.ui.fragment.booking_list.BookingListRepository
import com.youbook.glowpros.ui.fragment.booking_list.BookingListViewModel
import com.youbook.glowpros.ui.fragment.chat_list.ChatMessageRepository
import com.youbook.glowpros.ui.fragment.chat_list.ChatMessageViewModel
import com.youbook.glowpros.ui.fragment.dashboard.HomeRepository
import com.youbook.glowpros.ui.fragment.dashboard.HomeViewModel
import com.youbook.glowpros.ui.fragment.profile_frag.ProfileFragRepository
import com.youbook.glowpros.ui.fragment.profile_frag.ProfileFragViewModel

class ViewModelFactory(private val repository: BaseRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository as HomeRepository) as T
            modelClass.isAssignableFrom(BookingListViewModel::class.java) -> BookingListViewModel(repository as BookingListRepository) as T
            modelClass.isAssignableFrom(ProfileFragViewModel::class.java) -> ProfileFragViewModel(repository as ProfileFragRepository) as T
            modelClass.isAssignableFrom(ChatMessageViewModel::class.java) -> ChatMessageViewModel(repository as ChatMessageRepository) as T

            else -> throw IllegalAccessException("ViewModel Class Not Found!")
        }
    }

}