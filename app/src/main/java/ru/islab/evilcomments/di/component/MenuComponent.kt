package ru.islab.evilcomments.di.component

import dagger.Subcomponent
import ru.islab.evilcomments.di.module.MenuModule
import ru.islab.evilcomments.di.module.SignInModule
import ru.islab.evilcomments.di.scope.PerActivity
import ru.islab.evilcomments.presentation.menu.MenuActivity
import ru.islab.evilcomments.presentation.menu.MenuPresenter
import ru.islab.evilcomments.presentation.sign_in.SignInPresenter

@PerActivity
@Subcomponent(
    modules = [MenuModule::class]
)
interface MenuComponent {
    fun inject(menuPresenter: MenuPresenter)

    fun inject(menuActivity: MenuActivity)
}