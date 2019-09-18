package ru.trmedia.trbtlservice.comment.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.trmedia.trbtlservice.comment.data.database.AppDatabase
import ru.trmedia.trbtlservice.comment.data.database.AppDatabase.Companion.DATABASE_NAME
import ru.trmedia.trbtlservice.comment.data.database.FollowDao
import ru.trmedia.trbtlservice.comment.di.scope.PerApplication
import javax.inject.Named

@Module
class DatabaseModule {

    @Provides
    @PerApplication
    fun provideDatabase(@Named("AppContext") context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, DATABASE_NAME
        ).allowMainThreadQueries().build()
    }

    @Provides
    @PerApplication
    fun provideFollowDao(database: AppDatabase): FollowDao {
        return database.followDao()
    }
}