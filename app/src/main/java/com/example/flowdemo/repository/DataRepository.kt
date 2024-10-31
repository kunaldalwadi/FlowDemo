package com.example.flowdemo.repository

import com.example.flowdemo.data.Post
import com.example.flowdemo.network.ApiClient
import com.example.flowdemo.network.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class DataRepository(
    private val ioDispatcher: CoroutineDispatcher
) {

    fun makeLoginCall() {
    }

    fun makeSignupCall() {
    }

    fun makeForgotPasswordCall() {
    }

    fun makeLogoutCall() {
    }

    /**
     * This function wraps the service response into Result object.
     */
    suspend fun makeServiceCall(): Result<List<Post>> {
        return withContext(Dispatchers.IO) {
            Result.Success(ApiClient.apiService.getPosts())
        }
    }

    /**
     * This function wraps the service response into Result object and then emits using flow.
     * so basically it wraps the Result object further into flow.
     */
    suspend fun getDataFlow(): Flow<Result<List<Post>>> {
        return withContext(Dispatchers.IO) {
            flow {
                try {
                    emit(Result.Success(ApiClient.apiService.getPosts()))
                } catch (e: Exception) {
                    emit(Result.Error(Exception("Network was not really responding !!")))
                }
            }
        }
    }

    val sampleList = listOf<Post>(
        Post(id = 1, title = "This is Title Sample", userId = 1),
        Post(id = 2, title = "title2", userId = 2),
        Post(id = 3, title = "title3", userId = 3)
    )


    /**
     * This function wraps the service response into flow and emits it
     */
    suspend fun getPost2(): Flow<Post> {
        return withContext(ioDispatcher) {
            flow {
                emit(ApiClient.apiService.getPost2())
            }
        }
    }

    /**
     * This uses the below extension function to wrap the data received from the service call into Result object
     */
    suspend fun getPostAsResult(): Flow<Result<Post>> {
        return withContext(ioDispatcher) {
            flow {
                emit(ApiClient.apiService.getPost2())
            }.asResult()
        }
    }
}


/**
 * Extension function to convert a [Flow] into a [Result]
 */

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(Exception(it))) }
}