package ru.islab.evilcomments.presentation.menu

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface MenuView: MvpView{
    fun startNewGame()
    fun continueGame()
    fun sendOffer()
    fun creators()
    fun saveVersionCode()
    fun initUI()
}