package ru.trmedia.trbtlservice.comment.di.component

import dagger.Subcomponent
import ru.trmedia.trbtlservice.comment.presentation.InstaLoginPresenter
import ru.trmedia.trbtlservice.comment.di.module.InstaLoginModule
import ru.trmedia.trbtlservice.comment.di.scope.PerActivity

@PerActivity
@Subcomponent(
    modules = [InstaLoginModule::class]
)
interface InstaLoginComponent {
    fun inject(instaLoginPresenter: InstaLoginPresenter)
}