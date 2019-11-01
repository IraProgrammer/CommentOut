package ru.islab.evilcomments.presentation.sign_in

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface SignInView: MvpView{
    fun startGame()
    fun authorize()
    fun saveVersionCode()
}