package ru.islab.evilcomments.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.islab.evilcomments.domain.EvilComment
import ru.islab.evilcomments.domain.Follow
import ru.islab.evilcomments.domain.Punishment
import ru.islab.evilcomments.domain.VKUser

@Database(
    entities = [Follow::class, VKUser::class, EvilComment::class, Punishment::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun followDao(): FollowDao

    abstract fun vkUserDao(): VKUserDao

    abstract fun commentDao(): CommentDao

    abstract fun punishmentDao(): PunishmentDao

    companion object {
        const val DATABASE_NAME = "ISLab.InstaDatabase"
    }
}