package com.example.flowdemo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.flowdemo.data.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    // Simply, add suspend keyword to the function, to make it work with coroutines.
    @Insert
    suspend fun insert(post: Post)

    // No need to add suspend keyword, because it returns Flow, flows already uses coroutines.
    @Query("SELECT * FROM post_table")
    fun getAll(): Flow<List<Post>>

    // Simply, add suspend keyword to the function, to make it work with coroutines.
    @Delete
    suspend fun delete(post: Post)
}