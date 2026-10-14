package com.replica.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "watch_history",
    indices = [
        Index(value = ["media_id", "provider_id"]),
        Index(value = ["last_updated"])
    ]
)
data class WatchHistoryEntity(
    @PrimaryKey
    val id: String, // Composite hash: provider_id + media_id + episode_id
    @ColumnInfo(name = "media_id") val mediaId: String,
    @ColumnInfo(name = "provider_id") val providerId: String,
    @ColumnInfo(name = "episode_id") val episodeId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "poster_url") val posterUrl: String?,
    @ColumnInfo(name = "episode_index") val episodeIndex: Int,
    @ColumnInfo(name = "position_ms") val positionMs: Long,
    @ColumnInfo(name = "duration_ms") val durationMs: Long,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "download_tasks",
    indices = [Index(value = ["episode_id", "media_id"])]
)
data class DownloadTaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "media_id") val mediaId: String,
    @ColumnInfo(name = "episode_id") val episodeId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "stream_url") val streamUrl: String,
    @ColumnInfo(name = "local_uri") val localUri: String?,
    @ColumnInfo(name = "bytes_downloaded") val bytesDownloaded: Long = 0L,
    @ColumnInfo(name = "total_bytes") val totalBytes: Long = 0L,
    @ColumnInfo(name = "status") val status: DownloadStatus = DownloadStatus.QUEUED,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

enum class DownloadStatus {
    QUEUED, DOWNLOADING, PAUSED, COMPLETED, FAILED
}

@Entity(tableName = "provider_sources")
data class ProviderSourceEntity(
    @PrimaryKey
    @ColumnInfo(name = "provider_id") val providerId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "base_url") val baseUrl: String,
    @ColumnInfo(name = "api_version") val apiVersion: Int,
    @ColumnInfo(name = "is_enabled") val isEnabled: Boolean = true,
    @ColumnInfo(name = "source_type") val sourceType: String // e.g., "ANIME", "MOVIE", "MULTI"
)
