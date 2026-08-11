package com.example.model

enum class MediaType {
    MOVIE,
    SERIES,
    K_DRAMA,
    ANIME
}

data class DownloadLink(
    val serverName: String, // e.g., "Mega.nz", "VIP Direct", "Google Drive", "StreamVIP"
    val quality: String,    // e.g., "1080p 60fps", "720p HD", "4K HEVC"
    val size: String,       // e.g., "2.1 GB", "950 MB"
    val url: String,
    val isVIP: Boolean = false
)

data class EpisodeItem(
    val episodeNumber: Int,
    val title: String,
    val duration: String,
    val streamUrl: String,
    val downloadLinks: List<DownloadLink> = emptyList()
)

data class MediaItem(
    val id: String,
    val titleEn: String,
    val titleMm: String,
    val type: MediaType,
    val posterUrl: String,
    val bannerUrl: String,
    val rating: Float,
    val year: Int,
    val quality: String,
    val audioSubInfo: String, // e.g. "Burmese Subbed", "Dual Audio (Mm Sub)"
    val genres: List<String>,
    val duration: String,
    val synopsisEn: String,
    val synopsisMm: String,
    val cast: List<String>,
    val director: String,
    val downloadLinks: List<DownloadLink>,
    val episodes: List<EpisodeItem> = emptyList(),
    val trailerUrl: String,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isTopRated: Boolean = false
)
