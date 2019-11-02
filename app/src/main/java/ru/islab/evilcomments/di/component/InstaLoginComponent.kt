package ru.islab.evilcomments.di.component

import dagger.Subcomponent
import ru.islab.evilcomments.di.module.InstaLoginModule
import ru.islab.evilcomments.di.scope.PerActivity
import ru.islab.evilcomments.presentation.insta.InstaLoginActivity
import ru.islab.evilcomments.presentation.insta.InstaLoginPresenter

@PerActivity
@Subcomponent(
    modules = [InstaLoginModule::class]
)
interface InstaLoginComponent {
    fun inject(instaLoginPresenter: InstaLoginPresenter)

    fun inject(instaLoginActivity: InstaLoginActivity)
}