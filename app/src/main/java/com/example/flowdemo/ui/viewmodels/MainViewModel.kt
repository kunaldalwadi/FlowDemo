package com.example.flowdemo.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowdemo.data.Post
import com.example.flowdemo.network.Result
import com.example.flowdemo.repository.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val TAG = MainViewModel::class.java.simpleName


    //when you create a flow using StateFlow,
    //you will have to give an initial value right here,
    //and then you dont need to give an initial value while calling it in MainActivity.
    private val _count = MutableStateFlow(20)
    val count: StateFlow<Int> = _count.asStateFlow()

    private val _showProgress = MutableStateFlow(true)
    val showProgress: StateFlow<Boolean> = _showProgress.asStateFlow()


    //when you create a flow using a builder,
    //you will have to give an initial value when you call it in MainActivity.
    val countDownTimerDemo = flow<Int> {
        val startingValue = 10
        var currentValue = startingValue
        while (currentValue > 0) {
            emit(currentValue)
            delay(1000L)
            currentValue--
        }
    }


    /*
    Current understanding : the methods/function that is going to update the value of the flow,
    should not be private since they will be accessed by the UI layer/screens/UI elements
    */
    fun updateCount() {

        //one of the way to update the value of flow
        _count.update {
            it + 1
        }

        //another way of updating the value of flow
        _count.value += 1
    }

    fun toggleProgress() {
        _showProgress.update {
            !it
        }
    }


    private val _data = MutableStateFlow<List<Post>>(emptyList())
    val data: StateFlow<List<Post>> = _data.asStateFlow()

    private val _error = MutableStateFlow<String?>("")
    val error: StateFlow<String?> = _error.asStateFlow()

    fun getDataFromInternet() {
        viewModelScope.launch {
            val result = try {
                dataRepository.makeServiceCall()
            } catch (e: Exception) {
                Result.Error(Exception("Network request failed"))
            }
            Log.d(TAG, "getDataFromInternet: result : " + result.toString())
            when (result) {
                is Result.Success -> {
                    _data.value = result.data
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                }
                is Result.Loading -> {
                    _showProgress.value = true
                }
            }
        }
    }

    fun getDataFlowFromInternet() {
        viewModelScope.launch {
            dataRepository.getDataFlow().collect {
                when(it) {
                    is Result.Success -> {
                        _data.value = it.data
                    }
                    is Result.Error -> {
                        _error.value = it.exception.message
                    }
                    is Result.Loading -> {
                        _showProgress.value = true
                    }
                }
            }
        }
    }


    private val _postData = MutableStateFlow<Post>(Post(0, "", 0))
    val postData: StateFlow<Post> = _postData.asStateFlow()

    fun getSpecificPost() {
        viewModelScope.launch {
            dataRepository.getPost2()
                .catch { e ->
                    Log.d(TAG, "getSpecificPost: Exception : " + e.message)
                }
                .collect {
                    _postData.value = it
                }
        }
    }
}