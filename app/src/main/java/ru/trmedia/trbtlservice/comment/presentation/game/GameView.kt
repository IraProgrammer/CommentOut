package ru.trmedia.trbtlservice.comment.presentation.game

import moxy.MvpView
import ru.trmedia.trbtlservice.comment.domain.Follow
import ru.trmedia.trbtlservice.comment.presentation.OneModel

interface GameView : MvpView {
    fun onShowNextRaund(oneModel: OneModel)
}