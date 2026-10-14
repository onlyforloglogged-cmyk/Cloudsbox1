package com.replica.core.database.dao

import androidx.room.*
import com.replica.core.database.entity.DownloadStatus
import com.replica.core.database.entity.DownloadTaskEntity
import com.replica.core.database.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY last_updated DESC")
    fun getAllHistory(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE media_id = :mediaId AND provider_id = :providerId LIMIT 1")
    suspend fun getWatchPosition(mediaId: String, providerId: String): WatchHistoryEntity?

    @Upsert
    suspend fun upsertProgress(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE id = :id")
    suspend fun deleteEntry(id: String)
}

@Dao
interface DownloadDao {
    @Query("SELECT * FROM download_tasks ORDER BY created_at DESC")
    fun observeAllTasks(): Flow<List<DownloadTaskEntity>>

    @Query("SELECT * FROM download_tasks WHERE status = 'DOWNLOADING' OR status = 'QUEUED'")
    fun observeActiveTasks(): Flow<List<DownloadTaskEntity>>

    @Query("SELECT * FROM download_tasks WHERE task_id = :taskId")
    suspend fun getTaskById(taskId: String): DownloadTaskEntity?

    @Upsert
    suspend fun upsertTask(task: DownloadTaskEntity)

    @Query("UPDATE download_tasks SET bytes_downloaded = :downloaded, total_bytes = :total WHERE task_id = :taskId")
    suspend fun updateProgress(taskId: String, downloaded: Long, total: Long)

    @Query("UPDATE download_tasks SET status = :status WHERE task_id = :taskId")
    suspend fun updateStatus(taskId: String, status: DownloadStatus)
}
