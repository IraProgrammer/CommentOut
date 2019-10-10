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
import ru.islab.evilcomments.domain.EvilComment
import ru.islab.evilcomments.domain.Punishment
import java.util.concurrent.Executors


@Module
class DatabaseModule {

    @Provides
    @PerApplication
    fun provideDatabase(@Named("AppContext") context: Context): AppDatabase {

        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .build()
    }
}