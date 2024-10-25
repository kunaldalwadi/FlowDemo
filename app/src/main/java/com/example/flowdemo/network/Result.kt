package com.example.flowdemo.network

/**
 * Custom Result class to be used as wrapper for the response from the API or Room Database.
 */

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}