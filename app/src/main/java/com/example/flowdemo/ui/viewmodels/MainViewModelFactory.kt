package com.example.flowdemo.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flowdemo.repository.DataRepository

class MainViewModelFactory(
    private val dataRepository: DataRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}