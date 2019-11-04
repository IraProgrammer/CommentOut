package ru.islab.evilcomments.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.islab.evilcomments.data.database.AppDatabase
import ru.islab.evilcomments.data.database.AppDatabase.Companion.DATABASE_NAME
import ru.islab.evilcomments.di.scope.PerApplication
import javax.inject.Named
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import ru.islab.evilcomments.domain.EvilComment
import ru.islab.evilcomments.domain.Punishment
import java.util.concurrent.Executors


@Module
class DatabaseModule {

    @Provides
    @PerApplication
    fun provideDatabase(@Named("AppContext") context: Context): AppDatabase {

        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            //.fallbackToDestructiveMigration()
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    val MIGRATION_1_2 = object : Migration(1, 2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'VKUser' ('id' INTEGER NOT NULL DEFAULT 0, 'name' TEXT NOT NULL, 'photo' TEXT NOT NULL, 'canPost' INTEGER NOT NULL, PRIMARY KEY('id'))")
            database.execSQL("CREATE TABLE IF NOT EXISTS 'VKEvilComment' ('id' INTEGER NOT NULL DEFAULT 0, 'text' TEXT NOT NULL, PRIMARY KEY('id'))")
            database.execSQL("CREATE TABLE IF NOT EXISTS 'VKPunishment' ('id' INTEGER NOT NULL DEFAULT 0, 'text' TEXT NOT NULL, PRIMARY KEY('id'))")
        }
    }
}