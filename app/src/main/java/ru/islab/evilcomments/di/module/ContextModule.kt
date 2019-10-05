package ru.islab.evilcomments.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.islab.evilcomments.di.scope.PerApplication
import javax.inject.Named

@Module
class ContextModule(
    @get:Provides
    @get:PerApplication
    @get:Named("AppContext")
    internal val context: Context
)