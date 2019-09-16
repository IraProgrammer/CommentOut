package ru.trmedia.trbtlservice.comment.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.trmedia.trbtlservice.comment.domain.Follow

@Database(
    entities = [Follow::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun followDao(): FollowDao

    companion object {
        const val DATABASE_NAME = "ISLab.InstaDatabase"
    }
}