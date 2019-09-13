package ru.trmedia.trbtlservice.comment.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.trmedia.trbtlservice.comment.di.scope.PerApplication
import javax.inject.Named

@Module
class ContextModule(
    @get:Provides
    @get:PerApplication
    @get:Named("AppContext")
    internal val context: Context
)