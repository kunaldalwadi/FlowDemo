package com.example.flowdemo.repository

import com.example.flowdemo.data.Post
import com.example.flowdemo.network.ApiClient
import com.example.flowdemo.network.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    suspend fun makeServiceCall(): Result<List<Post>> {
        return withContext(Dispatchers.IO) {
            Result.Success(ApiClient.apiService.getPosts())
        }
    }

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


    suspend fun getPost2(): Flow<Post> {
        return withContext(ioDispatcher) {
            flow {
                emit(ApiClient.apiService.getPost2())
            }
        }
    }
}