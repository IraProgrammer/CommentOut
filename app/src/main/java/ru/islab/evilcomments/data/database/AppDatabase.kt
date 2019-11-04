package ru.islab.evilcomments.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.islab.evilcomments.domain.*

@Database(
    entities = [InstaUser::class, VKUser::class, EvilComment::class, Punishment::class, VKEvilComment::class, VKPunishment::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun instaUserDao(): InstaUserDao

    abstract fun vkUserDao(): VKUserDao

    abstract fun commentDao(): CommentDao

    abstract fun punishmentDao(): PunishmentDao

    abstract fun vkCommentDao(): VKCommentDao

    abstract fun vkPunishmentDao(): VKPunishmentDao

    companion object {
        const val DATABASE_NAME = "ISLab.InstaDatabase"
    }
}