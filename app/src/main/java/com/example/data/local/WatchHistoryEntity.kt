package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleMm: String,
    val posterUrl: String,
    val watchedAt: Long = System.currentTimeMillis(),
    val episodeProgress: String = "Full Movie"
)
