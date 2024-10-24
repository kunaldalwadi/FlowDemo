package com.example.flowdemo.network

import com.example.flowdemo.data.Post
import kotlinx.coroutines.flow.Flow
import okhttp3.Response
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>

    @GET("posts/2")
    suspend fun getPost2(): Post
}