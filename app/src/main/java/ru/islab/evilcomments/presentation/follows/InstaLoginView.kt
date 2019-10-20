package ru.islab.evilcomments.presentation.follows

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface InstaLoginView: MvpView{
    fun startGame()
    fun networkSuccessed()
    fun networkFailed()
    fun initUI()
    fun saveVersionCode()
}