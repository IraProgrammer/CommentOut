package ru.islab.evilcomments.di.component

import dagger.Subcomponent
import ru.islab.evilcomments.di.module.GameModule
import ru.islab.evilcomments.di.scope.PerActivity
import ru.islab.evilcomments.presentation.game.GameActivity
import ru.islab.evilcomments.presentation.game.GamePresenter

@PerActivity
@Subcomponent(
    modules = [GameModule::class]
)
interface GameComponent {
    fun inject(gamePresenter: GamePresenter)

    fun inject(gameActivity: GameActivity)
}