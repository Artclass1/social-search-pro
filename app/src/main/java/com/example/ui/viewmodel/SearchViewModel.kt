package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.SearchHistory
import com.example.data.model.SocialMediaPost
import com.example.data.network.GeminiSearchResponse
import com.example.data.repository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application,
    private val repository: SearchRepository
) : AndroidViewModel(application) {

    // Lists of posts & search history
    val allPosts: StateFlow<List<SocialMediaPost>> = repository.allPostsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val searchHistory: StateFlow<List<SearchHistory>> = repository.searchHistoryFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Search and analysis UI state
    private val _currentQuery = MutableStateFlow("")
    val currentQuery: StateFlow<String> = _currentQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchResult = MutableStateFlow<GeminiSearchResponse?>(null)
    val searchResult: StateFlow<GeminiSearchResponse?> = _searchResult.asStateFlow()

    private val _matchedPosts = MutableStateFlow<List<SocialMediaPost>>(emptyList())
    val matchedPosts: StateFlow<List<SocialMediaPost>> = _matchedPosts.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _thinkingModeEnabled = MutableStateFlow(false)
    val thinkingModeEnabled: StateFlow<Boolean> = _thinkingModeEnabled.asStateFlow()

    // Navigation and tab states
    private val _activeTab = MutableStateFlow(0) // 0: Search Engine, 1: Posts Directory, 2: History
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    init {
        // Automatically pre-populate database if empty on launch
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }
    }

    fun onQueryChanged(newQuery: String) {
        _currentQuery.value = newQuery
    }

    fun setActiveTab(index: Int) {
        _activeTab.value = index
    }

    fun setThinkingModeEnabled(enabled: Boolean) {
        _thinkingModeEnabled.value = enabled
    }

    fun executeSearch(prompt: String) {
        if (prompt.trim().isEmpty()) {
            _errorMessage.value = "Please enter a search prompt."
            return
        }

        viewModelScope.launch {
            _isSearching.value = true
            _errorMessage.value = null
            _searchResult.value = null
            _matchedPosts.value = emptyList()

            try {
                // Call Repository search engine with thinking mode selection
                val result = repository.executeSearch(prompt, _thinkingModeEnabled.value)
                _searchResult.value = result

                // Fetch details of matching posts
                if (result.matchedPostIds.isNotEmpty()) {
                    val posts = repository.getPostsByIds(result.matchedPostIds)
                    _matchedPosts.value = posts
                } else {
                    _matchedPosts.value = emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "An error occurred during search: ${e.localizedMessage}"
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun loadHistoryItem(item: SearchHistory) {
        _currentQuery.value = item.query
        _errorMessage.value = null
        _activeTab.value = 0 // Switch to search engine tab to view results

        val takeawaysList = item.keyTakeaways?.split("\n")?.filter { it.isNotEmpty() } ?: emptyList()
        val matchedIds = item.matchedPostIds?.split(",")?.filter { it.isNotEmpty() }?.mapNotNull { it.toIntOrNull() } ?: emptyList()

        _searchResult.value = GeminiSearchResponse(
            matchedPostIds = matchedIds,
            sentimentSummary = item.sentimentSummary ?: "Neutral",
            positivePercentage = item.positivePercent,
            negativePercentage = item.negativePercent,
            neutralPercentage = item.neutralPercent,
            keyTakeaways = takeawaysList,
            mediaInsights = if (matchedIds.isNotEmpty()) "Multimedia posts indicate high engagement. Visual elements drive significant discussion." else "No active media files were matched for this historic search.",
            detailedAnswer = item.aiAnswer ?: "No answer available."
        )

        viewModelScope.launch {
            if (matchedIds.isNotEmpty()) {
                _matchedPosts.value = repository.getPostsByIds(matchedIds)
            } else {
                _matchedPosts.value = emptyList()
            }
        }
    }

    fun createCustomPost(
        authorName: String,
        authorHandle: String,
        platform: String,
        content: String,
        mediaType: String,
        mediaUrl: String?,
        hashtags: String
    ) {
        viewModelScope.launch {
            val post = SocialMediaPost(
                authorName = authorName,
                authorHandle = authorHandle,
                authorAvatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100", // Default placeholder avatar
                platform = platform,
                content = content,
                timestamp = System.currentTimeMillis(),
                mediaType = mediaType,
                mediaUrl = if (mediaUrl?.trim().isNullOrEmpty()) null else mediaUrl,
                engagementLikes = (5..150).random(),
                engagementComments = (0..30).random(),
                engagementShares = (0..15).random(),
                hashtags = hashtags
            )
            repository.insertCustomPost(post)
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            repository.deletePost(postId)
            // If the deleted post was in current matched posts, filter it out
            _matchedPosts.value = _matchedPosts.value.filter { it.id != postId }
            val currentResult = _searchResult.value
            if (currentResult != null) {
                _searchResult.value = currentResult.copy(
                    matchedPostIds = currentResult.matchedPostIds.filter { it != postId }
                )
            }
        }
    }

    fun deleteHistoryItem(historyId: Int) {
        viewModelScope.launch {
            repository.deleteHistory(historyId)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun clearSearchState() {
        _searchResult.value = null
        _matchedPosts.value = emptyList()
        _currentQuery.value = ""
    }
}

// --- Simple Factory to inject SearchRepository ---
class SearchViewModelFactory(
    private val application: Application,
    private val repository: SearchRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
