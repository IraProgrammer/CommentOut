package ru.islab.evilcomments.presentation.game

import moxy.MvpView
import ru.islab.evilcomments.presentation.OneModel

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
    fun showToast()
}