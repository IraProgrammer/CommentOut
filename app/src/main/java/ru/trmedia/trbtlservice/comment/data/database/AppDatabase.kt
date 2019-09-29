package ru.trmedia.trbtlservice.comment.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.trmedia.trbtlservice.comment.domain.EvilComment
import ru.trmedia.trbtlservice.comment.domain.Follow
import ru.trmedia.trbtlservice.comment.domain.Punishment

@Database(
    entities = [Follow::class, EvilComment::class, Punishment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun followDao(): FollowDao

    abstract fun commentDao(): CommentDao

    abstract fun punishmentDao(): PunishmentDao

    companion object {
        const val DATABASE_NAME = "ISLab.InstaDatabase"
    }
}