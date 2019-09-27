package ru.trmedia.trbtlservice.comment.di.component

import dagger.Subcomponent
import ru.trmedia.trbtlservice.comment.di.module.InstaLoginModule
import ru.trmedia.trbtlservice.comment.di.scope.PerActivity
import ru.trmedia.trbtlservice.comment.presentation.follows.InstaLoginActivity
import ru.trmedia.trbtlservice.comment.presentation.follows.InstaLoginPresenter

@PerActivity
@Subcomponent(
    modules = [InstaLoginModule::class]
)
interface InstaLoginComponent {
    fun inject(instaLoginPresenter: InstaLoginPresenter)

    fun inject(instaLoginActivity: InstaLoginActivity)
}