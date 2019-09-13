package ru.trmedia.trbtlservice.comment.di

import dagger.Component
import ru.trmedia.trbtlservice.comment.di.component.InstaLoginComponent
import ru.trmedia.trbtlservice.comment.di.module.ContextModule
import ru.trmedia.trbtlservice.comment.di.module.InstaLoginModule
import ru.trmedia.trbtlservice.comment.di.module.NetworkModule
import ru.trmedia.trbtlservice.comment.di.scope.PerApplication

@PerApplication
@Component(
    modules = [ContextModule::class, NetworkModule::class]
)
interface AppComponent {
    fun addInstaLoginComponent(instaLoginModule: InstaLoginModule): InstaLoginComponent
}