package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val query: String,
    val timestamp: Long,
    val aiAnswer: String?,
    val sentimentSummary: String?,
    val positivePercent: Int = 0,
    val negativePercent: Int = 0,
    val neutralPercent: Int = 0,
    val keyTakeaways: String?, // Newline or comma-separated
    val matchedPostIds: String? // Comma-separated IDs of posts that matched
)
