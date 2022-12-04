package com.youbook.glowpros.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ReviewModelFactory(private val reviewRepository: ReviewRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReviewListViewModel(reviewRepository) as T
    }

}
