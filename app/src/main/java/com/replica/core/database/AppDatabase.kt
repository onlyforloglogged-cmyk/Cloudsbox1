package com.replica.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.replica.core.database.dao.DownloadDao
import com.replica.core.database.dao.WatchHistoryDao
import com.replica.core.database.entity.DownloadTaskEntity
import com.replica.core.database.entity.ProviderSourceEntity
import com.replica.core.database.entity.WatchHistoryEntity

@Database(
    entities = [
        WatchHistoryEntity::class,
        DownloadTaskEntity::class,
        ProviderSourceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun downloadDao(): DownloadDao
}
