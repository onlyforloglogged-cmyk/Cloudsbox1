package com.replica.feature.details

import com.replica.core.database.entity.DownloadStatus

sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    data class Success(
        val title: String,
        val posterUrl: String?,
        val description: String,
        val episodes: List<EpisodeItem>,
        val isBookmarked: Boolean,
        val lastWatchedEpisodeId: String? = null
    ) : DetailsUiState
    data class Error(val message: String) : DetailsUiState
}

data class EpisodeItem(
    val id: String,
    val title: String,
    val episodeNumber: Int,
    val downloadStatus: DownloadStatus = DownloadStatus.QUEUED
)

sealed interface DetailsIntent {
    data class LoadDetails(val mediaId: String, val providerId: String) : DetailsIntent
    data class PlayEpisode(val episode: EpisodeItem) : DetailsIntent
    data class QueueDownload(val episode: EpisodeItem) : DetailsIntent
    data object ToggleBookmark : DetailsIntent
}

sealed interface DetailsEffect {
    data class NavigateToPlayer(val episodeId: String, val streamUrl: String) : DetailsEffect
    data class ShowSnackbar(val message: String) : DetailsEffect
}
