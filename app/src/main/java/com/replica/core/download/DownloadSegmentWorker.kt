package com.replica.core.download

import android.content.Context
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.replica.ReplicaApp
import com.replica.core.database.dao.DownloadDao
import com.replica.core.database.entity.DownloadStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class DownloadSegmentWorker(
    private val context: Context,
    parameters: WorkerParameters,
    private val downloadDao: DownloadDao,
    private val httpClient: OkHttpClient
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val taskId = inputData.getString(KEY_TASK_ID) ?: return@withContext Result.failure()
        val task = downloadDao.getTaskById(taskId) ?: return@withContext Result.failure()

        val targetDir = File(context.filesDir, "media_cache/${task.mediaId}")
        if (!targetDir.exists()) targetDir.mkdirs()
        val outputFile = File(targetDir, "${task.episodeId}.mp4")

        var downloadedBytes = if (outputFile.exists()) outputFile.length() else 0L

        setForeground(createForegroundInfo(task.title, downloadedBytes, task.totalBytes))
        downloadDao.updateStatus(taskId, DownloadStatus.DOWNLOADING)

        val request = Request.Builder()
            .url(task.streamUrl)
            .header("Range", "bytes=$downloadedBytes-")
            .build()

        try {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful && response.code != 206) {
                    downloadDao.updateStatus(taskId, DownloadStatus.FAILED)
                    return@withContext Result.retry()
                }

                val body = response.body ?: return@withContext Result.failure()
                val totalLength = (response.header("Content-Length")?.toLongOrNull() ?: 0L) + downloadedBytes

                val outputStream = FileOutputStream(outputFile, true)
                val buffer = ByteArray(8 * 1024)
                var bytesRead: Int

                body.byteStream().use { input ->
                    outputStream.use { output ->
                        var lastProgressUpdate = System.currentTimeMillis()
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead

                            val now = System.currentTimeMillis()
                            if (now - lastProgressUpdate > 500) {
                                downloadDao.updateProgress(taskId, downloadedBytes, totalLength)
                                setForeground(createForegroundInfo(task.title, downloadedBytes, totalLength))
                                lastProgressUpdate = now
                            }
                        }
                    }
                }

                downloadDao.updateProgress(taskId, downloadedBytes, totalLength)
                downloadDao.updateStatus(taskId, DownloadStatus.COMPLETED)
                Result.success()
            }
        } catch (e: Exception) {
            downloadDao.updateStatus(taskId, DownloadStatus.FAILED)
            Result.retry()
        }
    }

    private fun createForegroundInfo(title: String, downloaded: Long, total: Long): ForegroundInfo {
        val progressPercent = if (total > 0) ((downloaded * 100) / total).toInt() else 0
        val notification = NotificationCompat.Builder(context, ReplicaApp.DOWNLOAD_CHANNEL_ID)
            .setContentTitle("Downloading: $title")
            .setContentText("$progressPercent% completed")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progressPercent, total <= 0)
            .setOngoing(true)
            .build()

        return ForegroundInfo(
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    companion object {
        const val KEY_TASK_ID = "arg_task_id"
        private const val NOTIFICATION_ID = 1002

        fun enqueueWork(workManager: WorkManager, taskId: String) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresStorageNotLow(true)
                .build()

            val request = OneTimeWorkRequestBuilder<DownloadSegmentWorker>()
                .setConstraints(constraints)
                .setInputData(workDataOf(KEY_TASK_ID to taskId))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, java.util.concurrent.TimeUnit.SECONDS)
                .build()

            workManager.enqueueUniqueWork(
                "download_$taskId",
                ExistingWorkPolicy.KEEP,
                request
            )
        }
    }
}
