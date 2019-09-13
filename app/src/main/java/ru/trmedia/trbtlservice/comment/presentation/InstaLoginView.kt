package ru.trmedia.trbtlservice.comment.presentation

import moxy.MvpView
import ru.trmedia.trbtlservice.comment.UserWrap

interface InstaLoginView: MvpView{
    fun onShowInfo(userWrap: UserWrap)
}