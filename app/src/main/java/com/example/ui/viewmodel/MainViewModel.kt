package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.MediaRepository
import com.example.model.EpisodeItem
import com.example.model.MediaItem
import com.example.model.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = MediaRepository(db.mediaDao())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow("All")
    val selectedGenre: StateFlow<String> = _selectedGenre.asStateFlow()

    private val _selectedType = MutableStateFlow<MediaType?>(null)
    val selectedType: StateFlow<MediaType?> = _selectedType.asStateFlow()

    private val _selectedQuality = MutableStateFlow("All")
    val selectedQuality: StateFlow<String> = _selectedQuality.asStateFlow()

    private val _selectedMedia = MutableStateFlow<MediaItem?>(null)
    val selectedMedia: StateFlow<MediaItem?> = _selectedMedia.asStateFlow()

    private val _activeStreamEpisode = MutableStateFlow<EpisodeItem?>(null)
    val activeStreamEpisode: StateFlow<EpisodeItem?> = _activeStreamEpisode.asStateFlow()

    private val _isPlayerVisible = MutableStateFlow(false)
    val isPlayerVisible: StateFlow<Boolean> = _isPlayerVisible.asStateFlow()

    val featuredList = repository.getFeaturedMedia()
    val trendingList = repository.getTrendingMedia()
    val topRatedList = repository.getTopRatedMedia()

    val watchlist = repository.watchlist.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val watchHistory = repository.watchHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredList: StateFlow<List<MediaItem>> = combine(
        _searchQuery,
        _selectedGenre,
        _selectedType,
        _selectedQuality
    ) { query, genre, type, quality ->
        repository.searchAndFilter(
            query = query,
            genre = genre,
            type = type,
            quality = quality
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.getSampleMediaList()
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onGenreSelect(genre: String) {
        _selectedGenre.value = genre
    }

    fun onTypeSelect(type: MediaType?) {
        _selectedType.value = type
    }

    fun onQualitySelect(quality: String) {
        _selectedQuality.value = quality
    }

    fun selectMedia(item: MediaItem?) {
        _selectedMedia.value = item
        if (item != null && item.episodes.isNotEmpty()) {
            _activeStreamEpisode.value = item.episodes.first()
        } else if (item == null) {
            _activeStreamEpisode.value = null
        }
    }

    fun selectEpisode(episode: EpisodeItem) {
        _activeStreamEpisode.value = episode
    }

    fun toggleWatchlist(item: MediaItem, currentlySaved: Boolean) {
        viewModelScope.launch {
            repository.toggleWatchlist(item, currentlySaved)
        }
    }

    fun updateWatchlistStatus(id: String, status: String) {
        viewModelScope.launch {
            repository.updateWatchlistStatus(id, status)
        }
    }

    fun recordHistory(item: MediaItem, progress: String = "Full Movie") {
        viewModelScope.launch {
            repository.addToHistory(item, progress)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun openPlayer(episode: EpisodeItem? = null) {
        if (episode != null) {
            _activeStreamEpisode.value = episode
        }
        _isPlayerVisible.value = true
        _selectedMedia.value?.let { recordHistory(it, episode?.title ?: "Full Movie") }
    }

    fun closePlayer() {
        _isPlayerVisible.value = false
    }

    fun isItemInWatchlist(id: String): Flow<Boolean> = repository.isInWatchlist(id)
}
