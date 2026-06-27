package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SearchHistory
import com.example.data.model.SocialMediaPost
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    // --- Social Media Posts ---
    @Query("SELECT * FROM social_media_posts ORDER BY timestamp DESC")
    fun getAllPostsFlow(): Flow<List<SocialMediaPost>>

    @Query("SELECT * FROM social_media_posts ORDER BY timestamp DESC")
    suspend fun getAllPosts(): List<SocialMediaPost>

    @Query("SELECT COUNT(*) FROM social_media_posts")
    suspend fun getPostCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: SocialMediaPost)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<SocialMediaPost>)

    @Query("DELETE FROM social_media_posts WHERE id = :id")
    suspend fun deletePostById(id: Int)

    @Query("SELECT * FROM social_media_posts WHERE id IN (:ids)")
    suspend fun getPostsByIds(ids: List<Int>): List<SocialMediaPost>

    // --- Search History ---
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    fun getSearchHistoryFlow(): Flow<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(history: SearchHistory)

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Int)

    @Query("DELETE FROM search_history")
    suspend fun clearHistory()
}
