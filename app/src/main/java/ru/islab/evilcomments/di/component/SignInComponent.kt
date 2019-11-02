package ru.islab.evilcomments.di.component

import dagger.Subcomponent
import ru.islab.evilcomments.di.module.SignInModule
import ru.islab.evilcomments.di.scope.PerActivity
import ru.islab.evilcomments.presentation.sign_in.SignInActivity
import ru.islab.evilcomments.presentation.sign_in.SignInPresenter

@PerActivity
@Subcomponent(
    modules = [SignInModule::class]
)
interface SignInComponent {
    fun inject(signInPresenter: SignInPresenter)

    fun inject(signInActivity: SignInActivity)
}