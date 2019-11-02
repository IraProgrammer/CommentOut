package ru.islab.evilcomments.di.module

import dagger.Module
import dagger.Provides
import ru.islab.evilcomments.di.scope.PerActivity
import ru.islab.evilcomments.domain.DataHelper

@Module
class MenuModule {
    @Provides
    @PerActivity
    fun provideDataHelper(): DataHelper {
        return DataHelper()
    }
}