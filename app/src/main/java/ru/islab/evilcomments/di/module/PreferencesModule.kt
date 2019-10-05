package ru.islab.evilcomments.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.islab.evilcomments.data.AppPreferences
import ru.islab.evilcomments.di.scope.PerApplication
import javax.inject.Named

@Module
class PreferencesModule {

    @Provides
    @PerApplication
    fun providePreferences(@Named("AppContext") context: Context): AppPreferences {
        return AppPreferences(context)
    }
}