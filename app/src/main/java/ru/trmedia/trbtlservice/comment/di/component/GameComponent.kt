package ru.trmedia.trbtlservice.comment.di.component

import dagger.Subcomponent
import ru.trmedia.trbtlservice.comment.di.module.GameModule
import ru.trmedia.trbtlservice.comment.di.scope.PerActivity
import ru.trmedia.trbtlservice.comment.presentation.game.GamePresenter

@PerActivity
@Subcomponent(
    modules = [GameModule::class]
)
interface GameComponent {
    fun inject(gamePresenter: GamePresenter)
}