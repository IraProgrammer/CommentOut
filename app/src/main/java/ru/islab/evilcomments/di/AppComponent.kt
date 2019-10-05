package ru.islab.evilcomments.di

import dagger.Component
import ru.islab.evilcomments.di.component.GameComponent
import ru.islab.evilcomments.di.component.InstaLoginComponent
import ru.islab.evilcomments.di.module.*
import ru.islab.evilcomments.di.scope.PerApplication

@PerApplication
@Component(
    modules = [ContextModule::class, DatabaseModule::class, PreferencesModule::class]
)
interface AppComponent {
    fun addInstaLoginComponent(instaLoginModule: InstaLoginModule): InstaLoginComponent

    fun addGameComponent(gameModule: GameModule): GameComponent
}