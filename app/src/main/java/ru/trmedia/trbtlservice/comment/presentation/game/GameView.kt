package ru.trmedia.trbtlservice.comment.presentation.game

import moxy.MvpView
import ru.trmedia.trbtlservice.comment.domain.Follow

interface GameView : MvpView {
    fun onShowRandomUser(follow: Follow)
}