package ru.trmedia.trbtlservice.comment.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.trmedia.trbtlservice.comment.data.AppPreferences
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