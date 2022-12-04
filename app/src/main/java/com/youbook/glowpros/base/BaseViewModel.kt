package com.youbook.glowpros.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.youbook.glowpros.repository.BaseRepository

abstract class BaseViewModel(
    private val repository: BaseRepository
) : ViewModel() {

        var profilePic = MutableLiveData("imagePath")
        var userName = MutableLiveData("userName")

   /* val userImage = MutableLiveData<String>()
    var image: String by Delegates.observable("") { _, _, newValue ->
        userImage.postValue(newValue)
    }*/

}