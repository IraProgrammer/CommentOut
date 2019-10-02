package ru.trmedia.trbtlservice.comment.presentation.game

import moxy.MvpView
import moxy.viewstate.strategy.StateStrategyType
import ru.trmedia.trbtlservice.comment.domain.Follow
import ru.trmedia.trbtlservice.comment.presentation.OneModel

interface GameView : MvpView {
    fun onShowNextRound(oneModel: OneModel)
    fun onStateRestored(oneModel: OneModel)
    fun onSetComment(comment: String)
    fun onSetPunishment(punishment: String)
    fun commentActive()
    fun punishmentActive()
    fun onSetPoints(points: Int)
    fun showGameOverDialog()
    fun onSetRound(round: Int)
}