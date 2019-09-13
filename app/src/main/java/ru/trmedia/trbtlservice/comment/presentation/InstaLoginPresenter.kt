package ru.trmedia.trbtlservice.comment.presentation

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.trmedia.trbtlservice.comment.App
import ru.trmedia.trbtlservice.comment.data.InstaApi
import ru.trmedia.trbtlservice.comment.di.module.InstaLoginModule
import javax.inject.Inject

@InjectViewState
class InstaLoginPresenter : MvpPresenter<InstaLoginView>() {

    @Inject
    lateinit var instaApi: InstaApi

    init {
        App.appComponent?.addInstaLoginComponent(InstaLoginModule())?.inject(this)
    }

    fun getUserInfoByAccessToken(authToken: String) {
        instaApi.getUserInfo(authToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ userWrap -> viewState.onShowInfo(userWrap) }, { throwable -> {} })
    }
}