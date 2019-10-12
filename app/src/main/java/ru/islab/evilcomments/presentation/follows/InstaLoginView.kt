package ru.islab.evilcomments.presentation.follows

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface InstaLoginView: MvpView{
    fun startGame()
    fun noNetworkInStart()
    fun networkFailed()
    fun networkSuccessed()
    fun initUI()
    fun saveVersionCode()
}