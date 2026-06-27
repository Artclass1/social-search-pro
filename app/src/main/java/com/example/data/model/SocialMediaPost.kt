package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "social_media_posts")
data class SocialMediaPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarUrl: String,
    val platform: String, // "Twitter", "Instagram", "Reddit", "LinkedIn", "YouTube"
    val content: String,
    val timestamp: Long,
    val mediaType: String, // "TEXT", "IMAGE", "VIDEO"
    val mediaUrl: String? = null,
    val engagementLikes: Int = 0,
    val engagementComments: Int = 0,
    val engagementShares: Int = 0,
    val hashtags: String = "" // Space or comma-separated hashtags
)
