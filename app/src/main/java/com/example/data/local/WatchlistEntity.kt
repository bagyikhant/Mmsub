package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleMm: String,
    val posterUrl: String,
    val rating: Float,
    val year: Int,
    val quality: String,
    val type: String,
    val addedAt: Long = System.currentTimeMillis(),
    val status: String = "PLAN_TO_WATCH" // PLAN_TO_WATCH, WATCHING, COMPLETED
)
