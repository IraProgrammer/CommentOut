package ru.islab.evilcomments.presentation.follows

import moxy.MvpView

interface InstaLoginView: MvpView{
    fun startGame()
    fun noNetworkInStart()
    fun networkFailed()
    fun networkSuccessed()
}