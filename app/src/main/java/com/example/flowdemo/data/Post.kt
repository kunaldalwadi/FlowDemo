package com.example.flowdemo.data


import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("userId")
    val userId: Int
)