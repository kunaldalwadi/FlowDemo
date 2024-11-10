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
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val TAG = MainViewModel::class.java.simpleName


    /** when you create a flow using StateFlow,
     * you will have to give an initial value right here,
     * and then you don't need to give an initial value while calling it in MainActivity.
     */
    private val _count = MutableStateFlow(20)
    val count: StateFlow<Int> = _count.asStateFlow()

    private val _showProgress = MutableStateFlow(true)
    val showProgress: StateFlow<Boolean> = _showProgress.asStateFlow()


    //when you create a flow using a builder,
    //you will have to give an initial value when you call it in MainActivity.
    val countDownTimerDemo = flow {
        val startingValue = 10
        var currentValue = startingValue
        while (currentValue > 0) {
            emit(currentValue)
            delay(1000L)
            currentValue--
        }
    }


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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun getDataFromInternet() {
        viewModelScope.launch {
            val result = try {
                dataRepository.makeServiceCall()
            } catch (e: Exception) {
                Result.Error(Exception("Network request failed -> " + e.message))
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
                when (it) {
                    is Result.Success -> {
                        _data.value = it.data
                        _error.value = null
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


    /**
     *  Best Practices from google docs :
     *  1. The ViewModel should create coroutines
     *  2. Don't expose mutable types
     *  3. The data and business layer should expose suspend functions and Flows
     *  4. Creating coroutines in the business and data layer
     *  5. Inject Dispatchers
     */

    private val _postData = MutableStateFlow<Post?>(null)
    val postData: StateFlow<Post?> = _postData.asStateFlow()

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

    fun getSpecificPostAsResult() {
        viewModelScope.launch {
            dataRepository.getPostAsResult().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _postData.value = result.data
                        _showProgress.value = false
                    }

                    is Result.Error -> {
                        Log.d(
                            TAG,
                            "getSpecificPostAsResult: Exception : " + result.exception.message
                        )
                        _showProgress.value = false
                    }

                    is Result.Loading -> {
                        _showProgress.value = true
                    }
                }
            }
        }
    }

//=============================================  Database region  ================================================================

    // This is a mutable state flow that will be used internally in the viewmodel, empty list is given as initial value.
    private val _favoritePosts = MutableStateFlow<List<Post>>(emptyList())

    //Immutable state flow that you expose to your UI
    val favoritePosts = _favoritePosts.asStateFlow()

    init {
        addDataToDatabase(dataRepository)
    }


    /**
     * This function is used to get all the books from the database, and update the value of favoriteBooks.
     * 1. viewModelScope.launch is used to launch a coroutine within the viewModel lifecycle.
     * 2. repository.getAll() is used to get all the books from the database.
     * 3. flowOn(Dispatchers.IO) is used to change the dispatcher of the flow to IO, which is optimal for IO operations, and does not block the main thread.
     * 4. collect is a suspending function used to collect the flow of books list, and assign the value to favoriteBooks.
     * 5. each time the flow emits a new value, the collect function will be called with the list of books.
     */
    fun getFavoritePosts() {
        viewModelScope.launch { //this: CoroutineScope
            dataRepository.getAllPosts().flowOn(Dispatchers.IO).collect { posts: List<Post> ->
                _favoritePosts.update { posts }
            }
        }
    }

    /**
     * This function is used to add a book to the database.
     * 1. viewModelScope.launch is used to launch a coroutine within the viewModel lifecycle.
     * 2. Dispatchers.IO is used to change the dispatcher of the coroutine to IO, which is optimal for IO operations, and does not block the main thread.
     * 3. repository.add(book) is used to add the book to the database.
     */
    fun addPost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            dataRepository.addPost(post)
        }
    }

    /**
     * This function is used to remove a book from the database.
     * 1. viewModelScope.launch is used to launch a coroutine within the viewModel lifecycle.
     * 2. Dispatchers.IO is used to change the dispatcher of the coroutine to IO, which is optimal for IO operations, and does not block the main thread.
     * 3. repository.remove(book) is used to remove the book from the database.
     */
    fun removePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) { //this: CoroutineScope
            dataRepository.removePost(post)
        }
    }


    /**
     * Adding data for testing.
     */
    private fun addDataToDatabase(dataRepository: DataRepository) {

        val postlist = listOf<Post>(
            Post(id = 1, title = "title1", userId = 1),
            Post(id = 2, title = "title2", userId = 2),
            Post(id = 3, title = "title3", userId = 3),
            Post(id = 4, title = "title4", userId = 4),
            Post(id = 5, title = "title5", userId = 5),
            Post(id = 6, title = "title6", userId = 6),
            Post(id = 7, title = "title7", userId = 7),
            Post(id = 8, title = "title8", userId = 8),
            Post(id = 9, title = "title9", userId = 9),
            Post(id = 10, title = "title10", userId = 10)
        )

        viewModelScope.launch(Dispatchers.IO) {
            for (post in postlist) {
                dataRepository.addPost(post)
            }
        }
    }

//=============================================  region end  ================================================================

}