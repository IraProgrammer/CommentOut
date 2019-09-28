package ru.trmedia.trbtlservice.comment.presentation.follows

import moxy.MvpView
import ru.trmedia.trbtlservice.comment.domain.UserWrap

interface InstaLoginView: MvpView{
    fun startGame()
    fun noNetworkInStart()
    fun networkFailed()
    fun networkSuccessed()
}