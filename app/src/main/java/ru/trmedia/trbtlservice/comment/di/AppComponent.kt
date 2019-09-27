package ru.trmedia.trbtlservice.comment.di

import dagger.Component
import ru.trmedia.trbtlservice.comment.di.component.GameComponent
import ru.trmedia.trbtlservice.comment.di.component.InstaLoginComponent
import ru.trmedia.trbtlservice.comment.di.module.*
import ru.trmedia.trbtlservice.comment.di.scope.PerApplication

@PerApplication
@Component(
    modules = [ContextModule::class, NetworkModule::class, DatabaseModule::class, PreferencesModule::class]
)
interface AppComponent {
    fun addInstaLoginComponent(instaLoginModule: InstaLoginModule): InstaLoginComponent

    fun addGameComponent(gameModule: GameModule): GameComponent
}