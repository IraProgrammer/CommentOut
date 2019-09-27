package ru.trmedia.trbtlservice.comment.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.trmedia.trbtlservice.comment.data.database.AppDatabase
import ru.trmedia.trbtlservice.comment.data.database.AppDatabase.Companion.DATABASE_NAME
import ru.trmedia.trbtlservice.comment.data.database.FollowDao
import ru.trmedia.trbtlservice.comment.data.network.AppPreferences
import ru.trmedia.trbtlservice.comment.di.scope.PerApplication
import javax.inject.Named

@Module
class PreferencesModule {

    @Provides
    @PerApplication
    fun providePreferences(@Named("AppContext") context: Context): AppPreferences {
        return AppPreferences(context)
    }
}